<template>
  <button @click="addVersion()">Add Version</button>
  <div class="center main">
  <table>
    <thead>
    <tr>
    <td>category</td><td>name</td><td>created</td><td>activated</td><td>action</td>
    </tr>
    </thead>
    <tbody>
    <tr v-for="version in versions">
      <!--<div class="blockquote blockList">
        <span>{{version.category.category}} | {{version.name}} | {{version.created}} | {{version.activated}}</span>
        <button @click="downloadVersion(version.version, version.name, version.category.category)">Download Version</button>

      </div>-->
      <td>{{version.category.category}}</td><td>{{version.name}}</td><td>{{new Date(version.created * 1000)}}</td>
      <td>{{new Date(version.activated * 1000)}}</td>
      <td>
        <button @click="downloadVersion(version.version, version.name, version.category.category)">Download Version</button>
        <button @click="activateVersion(version.version, version.name, version.category.category)">Activate Version</button>

      </td>
    </tr>
    </tbody>
  </table>
    <br>

      <div v-if="boolAdd">
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

  </div>
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

    function downloadVersion(version: bigint, name: string, category: string) {
      // http://localhost:8080/api/v1/files/versions/download?category=test&name=test&version=1
      const queryParams = `/download?category=${category}&name=${name}&version=${version}`
      //fetchData(`${uri}${queryParams}`, download)
      downloadDataAsHtmlFile(`${uri}${queryParams}`, `${name}_${version}`)
    }

    function activateVersion(version: bigint, name: string, category: string) {
      const fd = {
        category: category,
        name: name,
        version: version
      }
      console.log(fd)
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
      onFileSelected,
      activateVersion
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
  //display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
