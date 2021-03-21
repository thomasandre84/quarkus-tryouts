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

export function downloadDataAsHtmlFile(uri: string, filename: string) {
    axios.get(uri, { responseType: 'blob' })
        .then((res: any) => {
            const blob = new Blob([res.data], { type: 'text/html' })
            const link = document.createElement('a')
            link.href = URL.createObjectURL(blob)
            link.download = filename
            link.click()
            URL.revokeObjectURL(link.href)
        })
        .catch((err: any) => console.log(err))
}
