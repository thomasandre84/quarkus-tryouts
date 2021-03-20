<template>
  <button @click="addVersion()">Add Version</button>
  <div>
  <ul>
    <li v-for="version in versions">
      <div>
        {{version}}
      </div>
    </li>
    <br>
    <li v-if="boolAdd">
      <div>
        add a Version
        <form @submit.prevent="submitForm">
          <label for="catName">Category Name Dropdown</label>
          <select id="catName" v-model="catName">
            <option v-for="category in categories" value="category.name">{{category.name}}</option>
            <option value="usa">USA</option>
            <option value="india">India</option>
            <option value="uk">UK</option>
            <option value="germany">Germany</option>
          </select>
        </form>
      </div>
    </li>

  </ul>
  </div>
  <button @click="downloadVersion()">Download Version</button>
</template>

<script lang="ts">
import { ref, onMounted, watch } from 'vue'

export default {
  setup() {
    const versions = ref([])
    const categories = ref([])
    const axios = require('axios')
    const uri = '/v1/files/versions'
    const boolAdd = ref(false)
    const catName = ref('')

    function fetchVersions() {
      axios.get(uri)
      .then((res: any) => {
        versions.value = res.data
      })
      .catch((err: any) => console.log(err))
    }

    function fetchCategories() {
      axios.get('/v1/files/categories')
          .then((res: any) => categories.value = res.data)
          .catch((err: any) => console.log(err))
    }

    function addVersion() {
      boolAdd.value = ! boolAdd.value;
      if (boolAdd.value === true) {
        fetchCategories()
      }
    }

    function saveVersion() {
      const formData = new FormData()

      axios.post(uri, formData)
      .then((res: any) => console.log(res))
      .catch((err: any) => console.log(err))
    }

    function downloadVersion() {
      const queryParams = ''
      axios.get(`${uri}${queryParams}`)
      .then((res: any) => console.log(res))
      .catch((err: any) => console.log(err))
    }

    onMounted(() => {
      console.log('Mounting Categories')
      fetchVersions()
    })

    watch(versions, fetchVersions)

    return {
      versions,
      downloadVersion,
      addVersion,
      boolAdd,
      fetchCategories,
      catName,
      categories
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
