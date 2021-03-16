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
      <li v-for="cat in categories">
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
        <input type="text" name="catName" id="catName" v-model.trim="catName">
      <p>{{catName}}</p>
      <button @click="saveCategory()">Save Category</button>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
interface category {
  name: string,
  created: string
}
export default {
  setup() {
    const root = ref(null)
    const axios = require('axios')
    const categories: category[] = []
    let addCat = ref(false)
    let catName = ref('')


    function fetchCategories() {
      axios.get("/v1/files/categories")
          .then((res: any) => {
            //console.log(res);
            categories.length = 0;
            res.data.forEach((cat: category) => {
              categories.push(cat);
            })
          })
          .catch((err: any) => console.log(`Error: ${err}` ))
    }

    function logBaseUrl() {
      console.log(`VUE App API: ${process.env.VUE_APP_API}`);
    }

    function addCategory() {
      console.log(addCat)
      addCat.value = ! addCat.value;
    }

    function saveCategory() {
      console.log(`Save category: ${catName.value}`)
      axios.post('/v1/files/categories', {
        category: catName.value
      })
          .then((res: any) => console.log(res))
          .catch((err: any) => console.log(err))

      addCategory()
      fetchCategories()
    }

    onMounted(() => {
      fetchCategories()
    })

    return {
      categories,
      fetchCategories,
      addCategory,
      addCat,
      //boolAddCat
      catName,
      saveCategory
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
