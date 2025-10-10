import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AppState {
  // 侧边栏折叠状态
  collapsed: boolean
  // 主题模式
  theme: 'light' | 'dark'
  // 语言
  language: 'zh-CN' | 'en-US'
  // 设置侧边栏折叠状态
  toggleCollapsed: () => void
  // 设置主题
  setTheme: (theme: 'light' | 'dark') => void
  // 设置语言
  setLanguage: (language: 'zh-CN' | 'en-US') => void
}

export const useAppStore = create<AppState>()(
  persist(
    (set) => ({
      collapsed: false,
      theme: 'light',
      language: 'zh-CN',
      toggleCollapsed: () => set((state) => ({ collapsed: !state.collapsed })),
      setTheme: (theme) => set({ theme }),
      setLanguage: (language) => set({ language }),
    }),
    {
      name: 'app-store',
      partialize: (state) => ({
        collapsed: state.collapsed,
        theme: state.theme,
        language: state.language,
      }),
    }
  )
)