import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface GenerationTask {
  id: string
  type: 'image' | 'video' | 'audio'
  status: 'pending' | 'processing' | 'completed' | 'failed'
  prompt: string
  result?: string
  createdAt: number
  updatedAt: number
}

interface MultimodalState {
  tasks: GenerationTask[]
  currentTask: GenerationTask | null
  isGenerating: boolean
  addTask: (task: GenerationTask) => void
  updateTask: (id: string, updates: Partial<GenerationTask>) => void
  setCurrentTask: (task: GenerationTask | null) => void
  setGenerating: (generating: boolean) => void
  clearTasks: () => void
}

export const useMultimodalStore = create<MultimodalState>()(
  persist(
    (set) => ({
      tasks: [],
      currentTask: null,
      isGenerating: false,
      addTask: (task) => set((state) => ({ 
        tasks: [...state.tasks, task] 
      })),
      updateTask: (id, updates) => set((state) => ({
        tasks: state.tasks.map(task => 
          task.id === id ? { ...task, ...updates, updatedAt: Date.now() } : task
        ),
        currentTask: state.currentTask?.id === id 
          ? { ...state.currentTask, ...updates, updatedAt: Date.now() }
          : state.currentTask
      })),
      setCurrentTask: (currentTask) => set({ currentTask }),
      setGenerating: (isGenerating) => set({ isGenerating }),
      clearTasks: () => set({ tasks: [], currentTask: null })
    }),
    {
      name: 'multimodal-store'
    }
  )
)