

<template>
<div>
  <h1 style = "margin-left:4.5%; margin-top:-10px; margin-bottom:20px;">Recept</h1>

    <v-row>
        <Recept class="video-card" v-for="(value, index) in values" v-model="values[index]" v-bind:key="index" @delete="remove"/>
        <Recept class="video-card" :isNew="true" :editMode="true" v-model="newValue" @add="append" v-if="tick"/>
    </v-row>

</div>
</template>

<script>

const axios = require('axios').default;
import Recept from './Recept.vue';

export default {
  name: 'ReceptManager',

  components: {
    Recept
  },

  data: () => ({
    values: [],
    newValue: {},
    tick : true,
  }),

  async created() {
      var temp = await axios.get(axios.fixUrl('/recepts'))

      this.values = temp.data._embedded.recepts;

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
