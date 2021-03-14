<template>
  <div class="version">
    <h1>This is a Version page</h1>
    <button>
      <router-link to="/category">Category</router-link>
    </button>
    <versions></versions>
  </div>
</template>

<script lang="ts">
import Versions from '../components/Versions.vue'
import {Options, Vue} from "vue-class-component";

@Options({
  components: {
    Versions,
  },
})

export default class Version extends Vue {}
</script>
