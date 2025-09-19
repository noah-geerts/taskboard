import type { TaskRequestDto } from './TaskRequestDto'

export interface Task extends TaskRequestDto {
  tid: number
}

export type Status = 0 | 1 | 2
export type Priority = 0 | 1 | 2
