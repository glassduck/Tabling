![tabling-face-og](https://user-images.githubusercontent.com/55871108/168711756-3dd4153f-9e2e-4400-82ac-9878017c5a2b.png)
# 테이블링(Tabling)
본 예제는 MSA교육과정에서 배웠던 EventStorming 등의 내용을 포함한 실습 프로젝트입니다.

# Table of contents
- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현:](#구현)
  - [API Gateway]
  - [동기식 호출]
- [운영](#운영)
  - [오토 스케일링]
  - [셀프 힐링]
  - [무정지 배포]
  - [Config 및 PV 설정]



# 서비스 시나리오

1. 고객이 앱에서 가게를 검색한다.
2. 해당 가게의 이용가능한 좌석 수를 확인하고 줄서기를 누른다.
3. 줄서기를 누르면 가게에 이용 희망 내역이 전송된다.
4. 고객의 입장인원 수에 따라 대기 번호 부여 여부를 판단 후 그에 따라 카카오톡 메시지를 전달한다.


# 체크포인트
- [X] 이벤트 스토밍
- [X] 구현
  - [X] Gateway
  - [X] 동기식 호출
  - [X] Auto Scaling
  - [X] Self-healing
  - [X] Self-healing
  - [X] Zero-Downtime Deploy
  - [X] Config Map / Persistence Volume

# 분석설계
### 이벤트 스토밍
위 서비스 시나리오를 바탕으로 각각의 서비스에 대한 Event를 정의하고 그에 맞는 Context Bound를 설정한 결과는 다음과 같다.
<img width="1582" alt="Screen Shot 2022-05-17 at 11 07 09 AM" src="https://user-images.githubusercontent.com/55871108/168713780-c3f5bab3-64f7-4e8e-b961-910da5f69433.png">

# 구현
### API Gateway
원하는 서비스에 알맞게 할당될 수 있도록 포트를 분배
> application.yml
```
spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: Management
          uri: http://localhost:8081
          predicates:
            - Path=/orderMngs/** 
        - id: Recept
          uri: http://localhost:8082
          predicates:
            - Path=/recepts/** 
        - id: Seat
          uri: http://localhost:8083
          predicates:
            - Path=/seats/** 
        - id: ReceptDashBoard
          uri: http://localhost:8084
          predicates:
            - Path= 
        - id: frontend
          uri: http://localhost:8080
          predicates:
            - Path=/**
```

### FeignClient

checkSeat 부분은 위 앱의 핵심적인 기능으로서 조건 충족 여부에 따라 external 서비스인 카카오톡 메시지 서비스 작동 유무를 결정해야 함으로 Sync로 호출하기로 결정.
```
@FeignClient(name="Seat", url="http://Seat:8080")
public interface SeatService {
    @RequestMapping(method= RequestMethod.GET, path="/seats")
    public void checkSeat(@RequestBody Seat seat);{
    // 서비스 로직
    };
```

# 운영

### Auto Scaling
요청이 많아질 경우에 대비하여 pod의 다음과 같은 명령어로 auto Scaling을 설정할 필요가 있다.
평균 CPU의 사용률이 50%가 넘을 경우, 최대 5개까지 pod가 늘어날 수 있도록 설정 가정.
```
kubectl autoscale deployment Recept --cpu-percent=50 --min=3 --max=5
```


### Self-Healing(Liveness Probe)

Pod는 정상적으로 작동하지만 내부의 어플리케이션이 반응이 없다면, 컨테이너는 의미가 없다.
위와 같은 경우는 어플리케이션의 Deadlock 또는 메모리 과부화로 인해 발생할 수 있으며, 발생했을 경우 컨테이너를 다시 시작해야 한다.
Liveness probe는 Pod의 상태를 체크하다가, Pod의 상태가 비정상인 경우 kubelet을 통해서 재시작한다.
> deployment.yml
```
livenessProbe:
  httpGet:
    path: '/actuator/health'
    port: 8080
  initialDelaySeconds: 120
  timeoutSeconds: 2
  periodSeconds: 5
  failureThreshold: 5
```

### Zero-Downtime Deploy(Readiness Probe)
클러스터에 배포를 할때 readinessProbe 설정이 없으면 다운타임이 존재 하게 된다. 
이는 쿠버네티스에서 Ramped 배포 방식으로 무정지 배포를 시도 하지만, 서비스가 기동하는 시간이 있기 때문에, 기동 시간동안 장애가 발생. 
```
readinessProbe:
  httpGet:
    path: '/actuator/health'
    port: 8080
  initialDelaySeconds: 10
  timeoutSeconds: 2
  periodSeconds: 5
  failureThreshold: 10
```
### Config Map / Persistence Volume
쿠버네이트에서 mairadb 가 초기화 되지 않고 연속성이 구현 되지 위한 설정과 구현
#### Config Map
Deployment 설정에 직접 입력하는것은 개발자와 운영자사이에 역할이 혼재되므로, 운영자가 해당 설정 부분만을 관리할 수 있도록 별도의 Configuration 을 위한 쿠버네티스 객체인 ConfigMap (혹은 Secret)을 선언하여 연결
```
apiVersion: v1
kind: Secret
metadata:
  name: mysql-pass
type: Opaque
data:
  password: YWRtaW4=
```
#### Persistence Volume
Pod 사용하는 볼륨이 해당 Pod 에 기본 부착된 파일시스템이기 때문 db내용이 손실된다. 이를 해결하기 위하여 PersistenceVolume 으로 된 파일시스템에 연결하도록 설정.
```
spec:
  containers:
    volumeMounts:
    - name: k8s-mysql-storage
      mountPath: /var/lib/mysql
  volumes:
  - name: k8s-mysql-storage
    persistentVolumeClaim:
      claimName: "fs"
```
