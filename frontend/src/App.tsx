import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "./components/ui/table";

// Define the shape of a single todo item
interface TodoItem {
  id: number;
  name: string;
  status: "TODO" | "DONE" | "IN_PROGRESS";
}

function App() {
  // State for a single todo item since we're only fetching one todo for now
  const [todos, setTodos] = useState<TodoItem[] | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editedName, setEditedName] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);

  useEffect(() => {
    // Fetch a single todo item with ID 1
    const fetchTodo = async () => {
      try {
        const response = await fetch("http://localhost:8080/todoItems");
        console.log("what")
        if (!response.ok) throw new Error("Failed to fetch todo");
        const data = await response.json();
        setTodos(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch todo");
      } finally {
        setIsLoading(false);
      }
    };
    fetchTodo();
  }, []);

  // Handle name edit submission
  const handleNameSubmit = async (id: number, newName: string) => {
    try {
      const response = await fetch(`http://localhost:8080/todoItem/updateName?id=${id}&name=${newName}`, {
        method: 'PUT',
      });

      if (response.ok) {
        setTodos((prevTodos) =>
          prevTodos?.map((todo) =>
            todo.id === id ? { ...todo, name: newName } : todo
          ) || []
        );
        setIsEditing(false);
      } else {
        console.error('Failed to update the todo item');
      }
    } catch (error) {
      console.error('Error updating the todo item:', error);
    }
  };

  // Handle key press events for the edit input
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>, id: number) => {
    if (e.key === "Enter") {
      handleNameSubmit(id, editedName);
    } else if (e.key === "Escape") {
      setIsEditing(false);
      setEditedName(todos?.find(todo => todo.id === id)?.name || "");
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
      setTodos((prevTodos) => prevTodos?.filter((todo) => todo.id !== id) || []);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete todo");
    }
  };

  // Loading, error, and empty states for single todo
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

  if (!todos)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-500">No todo found</div>
      </div>
    );

  // Render a single todo item in a table format
  // This will be updated to show multiple todos when the API is available
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="container mx-auto py-10 px-4 max-w-4xl">
        <div className="bg-white rounded-lg shadow-lg p-6">
          <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">
            Todo Item
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
                  // if no todos available, show a message
                  <TableRow>
                    <TableCell className="text-center" colSpan={4}>No todo items available.</TableCell>
                  </TableRow>
                ) : (
                  // if there are rodos, render each todo as a row
                  todos.map((todo) => (
                    <TableRow key={todo.id}>
                      <TableCell className="text-center">{todo.id}</TableCell>
                      <TableCell
                        className="text-center cursor-pointer hover:bg-gray-50"
                        onClick={() => {
                          if (!isEditing) {
                            setIsEditing(true);
                            setEditedName(todo.name);
                            setEditingId(todo.id);
                          }
                        }}
                      >
                        {isEditing && editingId === todo.id ? (
                          <input
                            type="text"
                            value={editedName}
                            onChange={(e) => setEditedName(e.target.value)}
                            onKeyDown={(e) => handleKeyPress(e, todo.id)}
                            onBlur={() => handleNameSubmit(todo.id, editedName)}
                            className="w-full px-2 py-1 text-center border rounded"
                            autoFocus
                          />
                        ) : (
                          <span className="hover:text-blue-600">{todo.name}</span>
                        )}
                      </TableCell>
                      <TableCell className="text-center">
                        <span
                          className={`px-3 py-1 rounded-full text-sm ${todo.status === "DONE"
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
                          onClick={() => {
                            /* TODO: Implement status update */
                          }}
                          className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
                        >
                          Toggle Status
                        </button>
                      </TableCell>
                      <TableCell className="text-center">
                        <button
                          onClick={() => handleDelete(todo.id)}
                          className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                        >
                          Delete
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
