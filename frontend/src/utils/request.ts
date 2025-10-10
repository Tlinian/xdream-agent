import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'

// 请求配置
const config: AxiosRequestConfig = {
  baseURL: (import.meta as any).env?.VITE_API_BASE_URL || '',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
}

// 多模态服务请求配置
const multimodalConfig: AxiosRequestConfig = {
  baseURL: 'http://127.0.0.1:8085',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
}

// 创建axios实例
const instance: AxiosInstance = axios.create(config)

// 创建多模态服务axios实例
const multimodalInstance: AxiosInstance = axios.create(multimodalConfig)

// 请求拦截器
instance.interceptors.request.use(
  (config: any) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 多模态服务请求拦截器
multimodalInstance.interceptors.request.use(
  (config: any) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response: AxiosResponse) => {
    // 处理响应数据
    const { data } = response
    
    // 假设后端返回的数据格式为: { code: number|string, message: string, data: any }
    if (data.code === 200 || data.code === 0 || data.code === 'SUCCESS' || data.code === 'success') {
      return response
    } else {
      // 业务错误 - 抛出错误让调用方处理
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error: AxiosError) => {
    // 处理网络错误 - 抛出错误让调用方处理
    if (error.response) {
      const { status, data } = error.response
      
      // 根据状态码创建对应的错误信息
      let errorMessage = '请求失败'
      switch (status) {
        case 400:
          errorMessage = (data as any)?.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          errorMessage = '权限不足'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        case 503:
          errorMessage = '服务不可用'
          break
        default:
          errorMessage = (data as any)?.message || `请求失败: ${status}`
      }
      
      // 创建带有详细信息的错误对象
      const enhancedError = new Error(errorMessage)
      ;(enhancedError as any).status = status
      ;(enhancedError as any).data = data
      return Promise.reject(enhancedError)
    } else if (error.request) {
      const networkError = new Error('网络连接失败，请检查网络连接')
      ;(networkError as any).type = 'NETWORK_ERROR'
      return Promise.reject(networkError)
    } else {
      const configError = new Error('请求配置错误')
      ;(configError as any).type = 'CONFIG_ERROR'
      return Promise.reject(configError)
    }
  }
)

// 多模态服务响应拦截器
multimodalInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    // 处理响应数据
    const { data } = response
    
    // 假设后端返回的数据格式为: { code: number|string, message: string, data: any }
    if (data.code === 200 || data.code === 0 || data.code === 'SUCCESS' || data.code === 'success') {
      return response
    } else {
      // 业务错误 - 抛出错误让调用方处理
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error: AxiosError) => {
    // 处理网络错误 - 抛出错误让调用方处理
    if (error.response) {
      const { status, data } = error.response
      
      // 根据状态码创建对应的错误信息
      let errorMessage = '请求失败'
      switch (status) {
        case 400:
          errorMessage = (data as any)?.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          errorMessage = '权限不足'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        case 503:
          errorMessage = '服务不可用'
          break
        default:
          errorMessage = (data as any)?.message || `请求失败: ${status}`
      }
      
      // 创建带有详细信息的错误对象
      const enhancedError = new Error(errorMessage)
      ;(enhancedError as any).status = status
      ;(enhancedError as any).data = data
      return Promise.reject(enhancedError)
    } else if (error.request) {
      const networkError = new Error('网络连接失败，请检查网络连接')
      ;(networkError as any).type = 'NETWORK_ERROR'
      return Promise.reject(networkError)
    } else {
      const configError = new Error('请求配置错误')
      ;(configError as any).type = 'CONFIG_ERROR'
      return Promise.reject(configError)
    }
  }
)

// 封装请求方法
export const request = {
  get: <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return instance.get(url, { ...config, params })
  },
  
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return instance.post(url, data, config)
  },
  
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return instance.put(url, data, config)
  },
  
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return instance.delete(url, config)
  },
  
  upload: <T = any>(url: string, file: File, data?: any): Promise<AxiosResponse<T>> => {
    const formData = new FormData()
    formData.append('file', file)
    
    if (data) {
      Object.keys(data).forEach(key => {
        formData.append(key, data[key])
      })
    }
    
    return instance.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }
}

// 多模态服务请求方法
export const multimodalRequest = {
  get: <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return multimodalInstance.get(url, { ...config, params })
  },
  
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return multimodalInstance.post(url, data, config)
  },
  
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return multimodalInstance.put(url, data, config)
  },
  
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return multimodalInstance.delete(url, config)
  },
  
  upload: <T = any>(url: string, file: File, data?: any): Promise<AxiosResponse<T>> => {
    const formData = new FormData()
    formData.append('file', file)
    
    if (data) {
      Object.keys(data).forEach(key => {
        formData.append(key, data[key])
      })
    }
    
    return multimodalInstance.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }
}

export default instance