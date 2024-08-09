import apiClient from "@/api/apiClient";

export const uploadAudio = (formData) => {
  return apiClient.post("/transcriber/upload-audio", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const downloadFile = (path) => {
  return apiClient.get(`/transcriber/download?path=${path}`, {
    responseType: "blob",
  })
}