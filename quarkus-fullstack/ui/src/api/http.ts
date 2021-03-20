import axios from 'axios'

export function fetchData(uri: string, dataRef: any) {
    axios.get(uri)
        .then((res: any) => dataRef.value = res.data)
        .catch((err: any) => console.log(err))
}

export function postData(uri: string, formData: any) {
    axios.post(uri, formData)
        .catch((err: any) => console.log(err))
}

export function putData(uri: string, formData: any) {
    axios.put(uri, formData)
        .catch((err: any) => console.log(err))
}
