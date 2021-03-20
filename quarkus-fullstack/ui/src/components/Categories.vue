<template>
  <div class="hello">
    <button @click="addCategory()">Create Category</button>
    <div>
    <ul>
      <li v-for="cat in categories">
        <div class="mainContainer">
          <span>{{ cat.category }}</span> {{ cat.created }}
        </div>
      </li>
    </ul>
    </div>
    <br>
    <div v-if="addCat">
      <p>Add Category</p>
      <form @submit.prevent="submitForm">
        <label for="catName">Category Name</label>
        <input type="text" name="catName" id="catName" v-model="catName">
      <p>{{catName}}</p>
      <button @click="saveCat()">Save Category</button>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted, watch } from 'vue'

export default {
  setup() {
    const axios = require('axios')
    const uri = '/v1/files/categories'
    const addCat = ref(false)
    const catName = ref('')
    const categories = ref([])

    function addCategory() {
      console.log(addCat)
      addCat.value = ! addCat.value;
    }

    function saveCat() {
      console.log(`Save category: ${catName.value}`)
      const formData = {category: catName.value}
      axios.post( uri, formData)
      .then((res: any) => console.log(res))
      .catch((err: any) => console.log(err))
    }

    function fetchCategories() {
      axios.get(uri)
      .then((res: any) => categories.value = res.data)
      .catch((err: any) => console.log(err))
    }

    onMounted(() => {
      console.log('Mounting Categories')
      fetchCategories()
    })

    watch(categories, fetchCategories)

    return {
      categories,
      //fetchCategories,
      addCategory,
      addCat,
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
