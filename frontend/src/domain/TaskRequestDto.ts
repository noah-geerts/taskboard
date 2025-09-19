import type { Status, Priority } from './Task'

export interface TaskRequestDto {
  title: string
  description: string
  status: Status
  priority: Priority
}
