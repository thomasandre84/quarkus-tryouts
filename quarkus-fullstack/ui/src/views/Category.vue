<template>
  <div class="category">
    <h1>This is a category page</h1>
    <categories></categories>
  </div>
</template>

<script lang="ts">
  import Categories from '../components/Categories.vue'
  import {Options, Vue} from "vue-class-component";

  @Options({
    components: {
      Categories,
    },
  })

  export default class Category extends Vue {}
</script>
