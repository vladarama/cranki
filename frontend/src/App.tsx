import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "./components/ui/table";
import {TodoItemProps} from "./components/TodoItem";


function App() {
  // State for multiple todo items
  const [todos, setTodos] = useState<TodoItemProps[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState<number | null>(null); // Track which item is being edited
  const [editedName, setEditedName] = useState("");

  useEffect(() => {
    // Fetch all todo items from the server
    const fetchTodos = async () => {
      try {
        // Replace with your endpoint that fetches all todo items
        const response = await fetch("http://localhost:3306/todoItems");
        if (!response.ok) throw new Error("Failed to fetch todos");
        const data: TodoItemProps[] = await response.json();
        setTodos(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch todos");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodos();
  }, []);

  // Handle name edit submission
  const handleNameSubmit = async (id: number) => {
    const todo = todos.find((item) => item.id === id);
    if (!todo) return;

    try {
      const response = await fetch(
        `http://localhost:3306/todoItem/updateName?id=${id}&name=${editedName}`,
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

  // Handle key press events for the edit input
  const handleKeyPress = (
    e: React.KeyboardEvent<HTMLInputElement>,
    id: number
  ) => {
    if (e.key === "Enter") {
      handleNameSubmit(id);
    } else if (e.key === "Escape") {
      setIsEditing(null);
      const todo = todos.find((item) => item.id === id);
      setEditedName(todo?.name || "");
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
        `http://localhost:3306/todoItem/updateStatus?id=${id}&status=${newStatus}`,
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
                    <TableCell
                      colSpan={4}
                      className="text-center text-gray-500 py-4"
                    >
                      No todos found.
                    </TableCell>
                  </TableRow>
                ) : (
                  todos.map((todo) => (
                    <TableRow key={todo.id}>
                      <TableCell className="text-center">{todo.id}</TableCell>
                      <TableCell
                        className="text-center cursor-pointer hover:bg-gray-50"
                        onClick={() => {
                          if (isEditing !== todo.id) {
                            setIsEditing(todo.id);
                            setEditedName(todo.name);
                          }
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
                          <span className="hover:text-blue-600">
                            {todo.name}
                          </span>
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
                          onClick={() => toggleStatus(todo.id)}
                          className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
                        >
                          Toggle Status
                        </button>
                      </TableCell>
                    </TableRow>
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
