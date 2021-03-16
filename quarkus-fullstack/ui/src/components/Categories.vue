<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <!--<button @click="logBaseUrl()">logBaseUrl</button>-->
    <ul>
      <button @click="fetchCategories()">update Categories</button>
    </ul>
    <ul>
      <!--<li v-for="cat in categories"-->
    </ul>
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


    function fetchCategories() {
      axios.get("/v1/files/categories")
          .then((res: any) => JSON.stringify(res))
          .then((res: category[]) => {
            console.log(res);
            categories.length = 0;
            res.forEach((cat: category) => {
              categories.push(cat);
            })
          })
          .catch((err: any) => console.log(`Error: ${err}` ))
    }

    function logBaseUrl() {
      console.log(`VUE App API: ${process.env.VUE_APP_API}`);
    }

    onMounted(() => {
      console.log(fetchCategories())
    })

    return {
      categories,
      fetchCategories
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
