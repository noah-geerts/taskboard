import { useFilterContext } from '../GlobalProvider'
import { priorityColors, priorityMap } from '../utils/utils'

export default function Header() {
  const { query, setQuery, selectedPriorities, setSelectedPriorities } =
    useFilterContext()

  function handleOptionClick(
    e: React.MouseEvent<HTMLButtonElement>,
    optionText: number
  ) {
    e.stopPropagation()
    if (selectedPriorities.includes(optionText)) {
      setSelectedPriorities(
        selectedPriorities.filter((priority) => priority !== optionText)
      )
    } else {
      setSelectedPriorities([...selectedPriorities, optionText])
    }
  }

  return (
    <header className="flex flex-row justify-center items-center bg-[#f8f8f8] border-b-2 border-gray-100 gap-6 px-6 py-4">
      {/* Priority Filter Buttons */}
      <div className="flex flex-row gap-2">
        {[0, 1, 2].map((priority) => {
          const isSelected = selectedPriorities.includes(priority)
          return (
            <button
              key={priority}
              className={`flex items-center gap-2 px-3 py-2 rounded-md border-2 transition-all duration-200 ${
                isSelected
                  ? 'border-gray-400 bg-white shadow-sm'
                  : 'border-gray-200 bg-white hover:border-gray-300'
              }`}
              onClick={(e) => handleOptionClick(e, priority)}
            >
              <div
                className={`rounded-full ${priorityColors[priority]} w-3 h-3`}
              ></div>
              <span className="text-sm font-medium capitalize">
                {priorityMap[priority]}
              </span>
            </button>
          )
        })}
      </div>

      {/* Search Input */}
      <div className="flex items-center gap-2">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search tasks..."
          className="px-4 py-2 border-2 border-gray-200 rounded-md bg-white focus:outline-none focus:ring-0 focus:border-gray-400 transition-colors duration-200 min-w-[200px]"
        />
      </div>
    </header>
  )
}
