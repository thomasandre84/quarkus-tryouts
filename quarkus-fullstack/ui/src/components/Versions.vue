<template>
  <button @click="addVersion()">Add Version</button>
  <div>
  <ul>
    <li v-for="version in versions">
      <div>
        {{version}}
      </div>
      <br>
    </li>
    <br>
    <li v-if="boolAdd">
      <div>
        add a Version
        <form @submit.prevent="submitForm">
          <label for="catName">Category Name Dropdown</label>
          <select id="catName" v-model="catName">
            <option v-for="category in categories" v-bind:value="category.category">{{category.category}}</option>
          </select>
          <label for="versName">Version name</label>
          <input name="versName" id="versName" v-model="versName">
          <input type="file" @change="onFileSelected">
          <p>Category: {{catName}} and VersionName: {{versName}}</p>
          <button>Save Version</button>
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
import {fetchData, postData, putData, downloadDataAsHtmlFile} from '../api/http'

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
    const versName = ref('')
    let selectedFile: File

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
      const obj = {name: versName.value, category: catName.value}
      const formData = new FormData()
      formData.append('obj', JSON.stringify(obj));
      formData.append('file', selectedFile, selectedFile.name)
      console.log(formData)
      postData(uri, formData)
    }

    function downloadVersion() {
      // http://localhost:8080/api/v1/files/versions/download?category=test&name=test&version=1
      const queryParams = '/download?category=test&name=test&version=1'
      //fetchData(`${uri}${queryParams}`, download)
      downloadDataAsHtmlFile(`${uri}${queryParams}`, 'version')
    }

    function activateVersion() {
      const fd = {
        category: "string",
        name: "string",
        version: 1
      }
      putData(`${uri}/active`, fd)
    }

    function onFileSelected(event: any) {
      console.log(event)
      selectedFile = event.target.files[0]
    }

    function submitForm(event: any) {
      console.log('Form submited ' + event);
      console.log('Data: ' + versName.value + " " + catName.value)
      saveVersion()
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
      versName,
      categories,
      logDownload,
      submitForm,
      onFileSelected
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
