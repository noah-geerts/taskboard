export function debounce<T extends (...args: any[]) => void>(
  fn: T,
  delay: number
) {
  let timeout: ReturnType<typeof setTimeout>
  return function (this: ThisParameterType<T>, ...args: Parameters<T>) {
    if (timeout) clearTimeout(timeout)
    timeout = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

export const statusMap: Record<number, string> = {
  0: 'Todo',
  1: 'In Progress',
  2: 'Done',
}
export const priorityMap: Record<number, string> = {
  0: 'Secondary',
  1: 'Standard',
  2: 'Urgent',
}
export const priorityColors: Record<number, string> = {
  2: 'bg-red-500',
  1: 'bg-orange-400',
  0: 'bg-yellow-200',
}
