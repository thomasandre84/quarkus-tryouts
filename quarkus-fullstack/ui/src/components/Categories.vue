<template>
  <div class="hello">
    <!--<h1>{{ msg }}</h1>-->
    <!--<button @click="logBaseUrl()">logBaseUrl</button>-->
    <!--<ul>
      <button @click="fetchCategories()">update Categories</button>
    </ul>-->
    <button @click="addCategory()">Create Category</button>
    <div>
    <ul>
      <li v-for="cat in categors">
        <div class="mainContainer">
          <span>{{cat.category}}</span> {{cat.created}}
        </div>
      </li>
    </ul>
    </div>
    <br>
    <div v-if="addCat">
      <p>Add Category</p>
      <form @submit.prevent="submitForm">
        <label for="catName">Category Name</label>
        <input type="text" name="catName" id="catName" v-bind="catName">
      <p>{{catName}}</p>
      <button @click="saveCat()">Save Category</button>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import {fetchCategories, categories, saveCategory} from '../services/CategoryService'

export default {
  setup() {
    const root = ref(null)
    const axios = require('axios')
    //const categories: category[] = []
    const addCat = ref(false)
    const catName = ref('')
    const categors = ref(categories)

    function addCategory() {
      console.log(addCat)
      addCat.value = ! addCat.value;
    }

    function saveCat() {
      console.log(`Save category: ${catName.value}`)
      saveCategory(catName.value)
      addCategory()
      fetchCategories()
    }

    onMounted(() => {
      console.log('Mounting Categories')
      fetchCategories()
    })

    return {
      categors,
      //fetchCategories,
      addCategory,
      addCat,
      //boolAddCat
      catName,
      saveCat
    }
  }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
