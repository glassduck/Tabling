

<template>
<div>
  <h1 style = "margin-left:4.5%; margin-top:-10px; margin-bottom:20px;">OrderMng</h1>

    <v-row>
        <OrderMng class="video-card" v-for="(value, index) in values" v-model="values[index]" v-bind:key="index" @delete="remove"/>
        <OrderMng class="video-card" :isNew="true" :editMode="true" v-model="newValue" @add="append" v-if="tick"/>
    </v-row>

</div>
</template>

<script>

const axios = require('axios').default;
import OrderMng from './OrderMng.vue';

export default {
  name: 'OrderMngManager',

  components: {
    OrderMng
  },

  data: () => ({
    values: [],
    newValue: {},
    tick : true,
  }),

  async created() {
      var temp = await axios.get(axios.fixUrl('/orderMngs'))

      this.values = temp.data._embedded.orderMngs;

  },

  methods:{
    append(value){
      this.tick = false
      this.newValue = {}
      this.values.push(value)
      
      this.$nextTick(function(){
        this.tick=true
      })
    },

    remove(value){

      var where = -1;
      for(var i=0; i<this.values.length; i++){
        if(this.values[i]._links.self.href == value._links.self.href){
          where = i;
          break;
        }
      }

      if(where > -1){
        this.values.splice(i, 1);
      }
    }    
  }
};
</script>

<style>
  .video-card{width:300px; margin-left:4.5%; margin-top:50px; margin-bottom:50px;}
</style>
