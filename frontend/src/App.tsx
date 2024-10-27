
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
  const [todo, setTodo] = useState<TodoItem | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editedName, setEditedName] = useState("");

  useEffect(() => {
    // Fetch a single todo item with ID 1
    // Note: In the future, this will be expanded to fetch multiple todos
    const fetchTodo = async () => {
      try {
        // Currently using /todoItem/1 endpoint since the API for multiple todos is not available yet
        const response = await fetch("http://localhost:8080/todoItem/1");
        if (!response.ok) throw new Error("Failed to fetch todo");
        const data = await response.json();
        setTodo(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch todo");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodo();
  }, []);

  // Handle name edit submission
  const handleNameSubmit = async () => {
    if (!todo) return;

    try {
      const response = await fetch(
        `http://localhost:8080/todoItem/updateName?id=${todo.id}&name=${editedName}`,
        {
          method: "PUT",
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Update local state with new name
      setTodo((prev) => (prev ? { ...prev, name: editedName } : null));
      setIsEditing(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update name");
    }
  };

  // Handle key press events for the edit input
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleNameSubmit();
    } else if (e.key === "Escape") {
      setIsEditing(false);
      setEditedName(todo?.name || "");
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

  if (!todo)
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
                <TableRow>
                  <TableCell className="text-center">{todo.id}</TableCell>
                  <TableCell
                    className="text-center cursor-pointer hover:bg-gray-50"
                    onClick={() => {
                      if (!isEditing) {
                        setIsEditing(true);
                        setEditedName(todo.name);
                      }
                    }}
                  >
                    {isEditing ? (
                      <input
                        type="text"
                        value={editedName}
                        onChange={(e) => setEditedName(e.target.value)}
                        onKeyDown={handleKeyPress}
                        onBlur={handleNameSubmit}
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
                      onClick={() => {
                        /* TODO: Implement status update */
                      }}
                      className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
                    >
                      Toggle Status
                    </button>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
