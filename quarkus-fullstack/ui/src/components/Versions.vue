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
            <option v-for="category in categories" value="category.name">{{category.category}}</option>
          </select>

        </form>
      </div>
    </li>

  </ul>
  </div>
  <button @click="downloadVersion()">Download Version</button>
  <button @click="logDownload()">Log Download</button>
</template>

<script lang="ts">
import { ref, onMounted, watch } from 'vue'
import {fetchData, postData, putData} from '../api/http'

export default {
  setup() {
    const versions = ref([])
    const categories = ref([])
    //const axios = require('axios')
    const uri = '/v1/files/versions'
    const uriCategories = '/v1/files/categories'
    const boolAdd = ref(false)
    const catName = ref('')
    const download = ref(null)

    function fetchVersions() {
      fetchData(uri, versions)
    }

    function fetchCategories() {
      console.log('Fetching cagories')
      fetchData(uriCategories, categories)
    }

    function addVersion() {
      boolAdd.value = ! boolAdd.value;
      if (boolAdd.value === true) {
        fetchCategories()
      }
    }

    function saveVersion() {
      const formData = new FormData()
      postData(uri, formData)
    }

    function downloadVersion() {
      // http://localhost:8080/api/v1/files/versions/download?category=test&name=test&version=1
      const queryParams = '/download?category=test&name=test&version=1'
      fetchData(`${uri}${queryParams}`, download)
    }

    function logDownload() {
      console.log(download.value)
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
      //fetchCategories,
      catName,
      categories,
      logDownload
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
