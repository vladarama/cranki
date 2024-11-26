
import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from "./components/ui/table";
import TodoItem from "./components/TodoItem";
import { ChevronsUpDown } from "lucide-react";
import { Button } from "./components/ui/button";
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";


// Define the shape of a single todo item
interface TodoItem {
  id: number;
  name: string;
  description: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  priority: "LOW" | "MEDIUM" | "HIGH";
}

function App() {
  // State for multiple todo items
  const [todos, setTodos] = useState<TodoItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState<number | null>(null);
  const [editedName, setEditedName] = useState("");

  const [newTodo, setNewTodo] = useState({
    name: "",
    description: "",
    priority: "MEDIUM",
  });
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");
  const [selectedTodo, setSelectedTodo] = useState<TodoItem | null>(null); // New state for selected todo


  useEffect(() => {
    // Fetch multiple todo items
    const fetchTodos = async () => {
      try {
        const response = await fetch("http://localhost:8080/todoItems");
        if (!response.ok) throw new Error("Failed to fetch todos");
        const data: TodoItem[] = await response.json();
        // Sort by priority using current sortDirection
        const sortedData = data.sort((a, b) => {
          const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 };
          const comparison =
            priorityOrder[b.priority] - priorityOrder[a.priority];
          return sortDirection === "asc" ? -comparison : comparison;
        });
        setTodos(sortedData);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch todos");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodos();
  }, [sortDirection]);

  // Handle adding a new todo item
  // Currently assumes that there is a todo list called 'Tasks'
  const handleAddTodo = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/todoLists/Tasks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: newTodo.name,
          description: newTodo.description,
          priority: newTodo.priority,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      const createdTodo: TodoItem = await response.json();
      setTodos((prev) => [...prev, createdTodo]);
      setNewTodo({ name: "", description: "", priority: "MEDIUM" });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create todo");
    }
  };

  // Handle name edit submission
  const handleNameSubmit = async (id: number) => {
    if (!todos) return;

    try {
      const response = await fetch(
        `http://localhost:8080/todoItem/updateName?id=${id}&name=${editedName}`,
        {
          method: "PUT",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Update local state with new name
      setTodos((prev) =>
        prev.map((item) =>
          item.id === id ? { ...item, name: editedName } : item
        )
      );
      setIsEditing(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update name");
    }
  };

  // Toggle the status of a todo item
  const toggleStatus = async (id: number) => {
    const todo = todos.find((item) => item.id === id);
    if (!todo) return;

    const newStatus =
      todo.status === "NOT_DONE"
        ? "IN_PROGRESS"
        : todo.status === "IN_PROGRESS"
        ? "DONE"
        : "NOT_DONE";

    try {
      const response = await fetch(
        `http://localhost:8080/todoItem/updateStatus?id=${id}&status=${newStatus}`,
        {
          method: "PUT",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Update local state with new status
      setTodos((prev) =>
        prev.map((item) =>
          item.id === id ? { ...item, status: newStatus } : item
        )
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update status");
    }
  };

  // Handle delete todo item
  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`http://localhost:8080/todoItem/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Clear todo state after deletion
      setTodos((prev) => prev.filter((item) => item.id !== id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete todo");
    }
  };

  // Handle key press events for the edit input
  const handleKeyPress = (
    e: React.KeyboardEvent<HTMLInputElement>,
    id: number
  ) => {
    if (e.key === "Enter") {
      handleNameSubmit(id);
    } else if (e.key === "Escape") {
      setIsEditing(null);
      setEditedName("");
    }
  };

  // Add this function in the App component
  const handlePriorityChange = async (
    id: number,
    priority: "LOW" | "MEDIUM" | "HIGH"
  ) => {
    try {
      const response = await fetch(
        `http://localhost:8080/todoItem/updatePriority?id=${id}&priority=${priority}`,
        {
          method: "PUT",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Update local state with new priority
      setTodos((prev) =>
        prev.map((item) => (item.id === id ? { ...item, priority } : item))
      );
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to update priority"
      );
    }
  };

  const toggleSort = () => {
    const newDirection = sortDirection === "asc" ? "desc" : "asc";
    setSortDirection(newDirection);

    setTodos((prev) =>
      [...prev].sort((a, b) => {
        const priorityOrder = { HIGH: 3, MEDIUM: 2, LOW: 1 };
        const comparison =
          priorityOrder[b.priority] - priorityOrder[a.priority];
        return newDirection === "asc" ? -comparison : comparison;
      })
    );
  };

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (active.id !== over?.id) {
      setTodos((items) => {
        const oldIndex = items.findIndex((item) => item.id === active.id);
        const newIndex = over
          ? items.findIndex((item) => item.id === over.id)
          : -1;

        return arrayMove(items, oldIndex, newIndex);
      });
    }
  };

  // Loading, error, and empty states for multiple todos
  if (isLoading)
    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <div className="animate-pulse text-lg">Loading...</div>
      </div>
    );

  if (error)
    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <div className="text-red-500 text-center">Error: {error}</div>
      </div>
    );

  if (todos.length === 0)
    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <div className="text-gray-500">No todos found</div>
      </div>
    );

  // Render multiple todo items in a table format
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="w-full max-w-4xl mx-auto py-6 sm:py-10">
        {/* Add Todo Form */}
        <form onSubmit={handleAddTodo} className="mb-6 px-4">
          <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-end">
            <div className="flex-1">
              <label
                htmlFor="name"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Task Name
              </label>
              <input
                id="name"
                type="text"
                placeholder="Enter task name"
                value={newTodo.name}
                onChange={(e) =>
                  setNewTodo({ ...newTodo, name: e.target.value })
                }
                className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>
            <div className="flex-1">
              <label
                htmlFor="description"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Description
              </label>
              <input
                id="description"
                type="text"
                placeholder="Enter description"
                value={newTodo.description}
                onChange={(e) =>
                  setNewTodo({ ...newTodo, description: e.target.value })
                }
                className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div className="flex-1">
              <label
                htmlFor="priority"
                className="block text-sm font-medium text-gray-700 mb-1"
              >
                Priority
              </label>
              <select
                id="priority"
                value={newTodo.priority}
                onChange={(e) =>
                  setNewTodo({ ...newTodo, priority: e.target.value })
                }
                className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
            <button
              type="submit"
              className="w-full sm:w-auto px-6 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            >
              Add Todo
            </button>
          </div>
        </form>

        {/* Todo Item List*/}
        <div className="bg-white rounded-lg shadow-lg p-4 sm:p-6">
          <h1 className="text-2xl sm:text-3xl font-bold mb-6 text-center text-gray-800">
            Todo Items
          </h1>
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50">
                  <TableHead className="w-[50px]"> </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    ID
                  </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    Name
                  </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    Status
                  </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    <div className="flex items-center justify-center gap-2">
                      Priority
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
                        onClick={toggleSort}
                        title={`Sort by priority ${
                          sortDirection === "asc" ? "descending" : "ascending"
                        }`}
                      >
                        <ChevronsUpDown className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    Status Toggle
                  </TableHead>
                  <TableHead className="text-center whitespace-nowrap">
                    Delete
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                <DndContext
                  sensors={sensors}
                  collisionDetection={closestCenter}
                  onDragEnd={handleDragEnd}
                >
                  <SortableContext
                    items={todos.map((todo) => todo.id)}
                    strategy={verticalListSortingStrategy}
                  >
                    {todos.map((todo) => (
                      <TodoItem
                        key={todo.id}
                        {...todo}
                        isEditing={isEditing === todo.id}
                        editedName={editedName}
                        onStatusToggle={() => toggleStatus(todo.id)}
                        onDelete={() => handleDelete(todo.id)}
                        onPriorityChange={(priority) =>
                          handlePriorityChange(todo.id, priority)
                        }
                        onNameEdit={() => {
                          setIsEditing(todo.id);
                          setEditedName(todo.name);
                        }}
                        onNameChange={setEditedName}
                        onNameSubmit={() => handleNameSubmit(todo.id)}
                        onKeyPress={(e) => handleKeyPress(e, todo.id)}
                      />
                    ))}
                  </SortableContext>
                </DndContext>
        
                {todos.map((todo) => (
                  <TableRow key={todo.id} className="cursor-pointer" onClick={() => setSelectedTodo(todo)}>
                  <TableCell className="text-center">{todo.id}</TableCell>
                  <TableCell
                    className="text-center cursor-pointer hover:bg-gray-50"
                    onClick={(e) => {
                      e.stopPropagation(); // Prevent row click from opening detail view
                      setIsEditing(todo.id);
                      setEditedName(todo.name);
                    }}
                  >
                    {isEditing === todo.id ? (
                      <input
                        type="text"
                        value={editedName}
                        onChange={(e) => setEditedName(e.target.value)}
                        onKeyDown={(e) => handleKeyPress(e, todo.id)}
                        onBlur={() => handleNameSubmit(todo.id)}
                        className="w-full px-2 py-1 text-center border rounded"
                        autoFocus
                      />
                    ) : (
                      <span className="hover:text-blue-600">{todo.name}</span>
                    )}
                  </TableCell>
                    <TableCell className="text-center">
                      <span
                        className={`px-3 py-1 rounded-full text-sm ${
                          todo.status === "DONE"
                            ? "bg-green-100 text-green-800"
                            : todo.status === "IN_PROGRESS"
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-gray-100 text-gray-800"
                        }`}
                      >
                        {todo.status}
                      </span>
                    </TableCell>
                    <TableCell className="text-center">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          toggleStatus(todo.id);
                        }}
                        className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
                      >
                        Toggle Status
                      </button>
                    </TableCell>
                    <TableCell className="text-center">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(todo.id);
                        }}
                        className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                      >
                        Delete
                      </button>
                    </TableCell>
                  </TableRow>
                ))}
           </TableBody>
            </Table>
          </div>
        </div>
      </div>
      {selectedTodo && (
        <div className="fixed inset-0 bg-white z-50 flex items-center justify-center">
          <div className="p-8 bg-gray-100 shadow-lg rounded-lg text-center w-3/4 max-w-2xl">
            <button
              onClick={() => setSelectedTodo(null)}
              className="text-red-500 hover:text-red-700 text-lg mb-6"
            >
              Close
            </button>
            <div className="text-3xl font-bold mb-4">Todo Details</div>
            <div className="text-lg">
              <p className="mb-4">
                <strong>ID:</strong> {selectedTodo.id}
              </p>
              <p className="mb-4">
                <strong>Name:</strong> {selectedTodo.name}
              </p>
              <p className="mb-4">
                <strong>Status:</strong>{" "}
                <span
                  className={`px-3 py-1 rounded-full text-sm ${
                    selectedTodo.status === "DONE"
                      ? "bg-green-100 text-green-800"
                      : selectedTodo.status === "IN_PROGRESS"
                      ? "bg-yellow-100 text-yellow-800"
                      : "bg-gray-100 text-gray-800"
                  }`}
                >
                  {selectedTodo.status}
                </span>
              </p>
              <p className="mb-4">
                <strong>Description:</strong> {selectedTodo.description}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
