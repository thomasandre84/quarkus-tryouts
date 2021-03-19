const uri: string = '/v1/files/categories'

export interface category {
  name: string,
  created: string
}
const axios = require('axios')
export const categories: category[] = []

export const fetchCategories = () => {
  axios.get(uri)
          .then((res: any) => {
            //console.log(res);
            categories.length = 0;
            res.data.forEach((cat: category) => {
              categories.push(cat);
            })
          })
          .catch((err: any) => console.log(`Error: ${err}` ))
    }
export const saveCategory = (category: string) => {
    axios.post(uri, {category: category})
        .then((res: any) => console.log(res))
        .catch((err: any) => console.log(err))
}