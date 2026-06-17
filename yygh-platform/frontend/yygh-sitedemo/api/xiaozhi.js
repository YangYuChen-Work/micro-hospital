import axios from 'axios'

const chatService = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 60000
})

export function sendChatMessage(memoryId, message, onProgress) {
  return chatService.post('/xiaozhi/chat', { memoryId, message }, {
    responseType: 'stream',
    onDownloadProgress: onProgress
  })
}
