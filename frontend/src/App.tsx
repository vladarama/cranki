import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "./components/ui/table";
 import TodoItem from "./components/TodoItem";

// Define the shape of a single todo item (renamed to avoid conflicts)
interface Todo {
  id: number;
  name: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  description: string;
}

function App() {
  // State for multiple todo items
  const [todos, setTodos] = useState<Todo[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newTodo, setNewTodo] = useState({ name: "", status: "NOT_DONE", description: "" });

  useEffect(() => {
    // Fetch all todo items from the server
    const fetchTodos = async () => {
      try {
        const response = await fetch("http://localhost:8080/todoItems");
        if (!response.ok) throw new Error("Failed to fetch todos");
        const data: Todo[] = await response.json();
        setTodos(data);
      } catch (err) {
        setError("Failed to fetch todos.");
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodos();
  }, []);


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

  // Loading, error, and empty states for multiple todos
  if (isLoading)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-pulse text-lg">Loading...</div>
      </div>
    );

  if (error)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-red-500">Error: {error}</div>
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="container mx-auto py-10 px-4 max-w-4xl">
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">
            Todo Items
          </h1>
          <div className="flex justify-center">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50">
                  <TableHead className="text-center">ID</TableHead>
                  <TableHead className="text-center">Name</TableHead>
                  <TableHead className="text-center">Status</TableHead>
                  <TableHead className="text-center">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
              {todos.length === 0 ? (
    <TableRow>
      <TableCell colSpan={4} className="text-center text-gray-500 py-4">
        No todos found.
      </TableCell>
    </TableRow>
  ) : (
    todos.map((todo) => (
      <TodoItem
        key={todo.id}
        id={todo.id}
        name={todo.name}
        status={todo.status}
        description={todo.description}
        onStatusToggle={() => toggleStatus(todo.id)} // Pass the toggleStatus function with the todo.id
      />
    ))
  )}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
