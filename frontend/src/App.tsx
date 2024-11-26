import { useEffect, useState } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from "./components/ui/table";
import TodoDetailView from "./components/TodoDetailView"; // Import the detail view component

// Define the shape of a single todo item
interface TodoItem {
  id: number;
  name: string;
  description: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  properties: { [key: string]: string }; // Add properties field
}

interface Property {
  id: number;
  name: string;
}

function App() {
  // State for multiple todo items
  const [todos, setTodos] = useState<TodoItem[]>([]);
  const [properties, setProperties] = useState<Property[]>([]); // State for properties
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState<number | null>(null);
  const [editedName, setEditedName] = useState("");
  const [newTodo, setNewTodo] = useState({ name: "", description: "" });
  const [selectedTodo, setSelectedTodo] = useState<TodoItem | null>(null); // New state for selected todo
  const [newProperty, setNewProperty] = useState({ name: "", type: "LITERAL" });

  // Fetch properties for the todo list
  const fetchProperties = async () => {
    try {
      const response = await fetch("http://localhost:8080/todolist/Tasks/properties");
      if (!response.ok) throw new Error("Failed to fetch properties");
      const data: Property[] = await response.json();
      console.log(data)
      setProperties(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to fetch properties");
    }
  };

  useEffect(() => {
    // Fetch multiple todo items
    const fetchTodos = async () => {
      try {
        const response = await fetch("http://localhost:8080/todoItems");
        if (!response.ok) throw new Error("Failed to fetch todos");
        const data: TodoItem[] = await response.json();
        setTodos(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch todos");
      } finally {
        setIsLoading(false);
      }
    };

    fetchTodos();
    fetchProperties();
  }, []);

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
        body: JSON.stringify(newTodo),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      const createdTodo: TodoItem = await response.json();
      setTodos((prev) => [...prev, createdTodo]);
      setNewTodo({ name: "", description: "" });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create todo");
    }
  };

  // Function to handle adding a new property
  const handleAddProperty = async () => {
    try {
      const response = await fetch("http://localhost:8080/property", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: newProperty.name,
          type: newProperty.type,
          todoListId: 4, // Assuming the todo list ID is 1, change as needed
          values: [] // Add values field if needed
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Refresh properties after adding new property
      fetchProperties();
      setNewProperty({ name: "", type: "LITERAL" });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create property");
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
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>, id: number) => {
    if (e.key === "Enter") {
      handleNameSubmit(id);
    } else if (e.key === "Escape") {
      setIsEditing(null);
      setEditedName("");
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

  if (todos.length === 0)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-500">No todos found</div>
      </div>
    );

  // Render multiple todo items in a table format
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="container mx-auto py-10 px-4 max-w-4xl">
        {/* Add Todo Form */}
        <form onSubmit={handleAddTodo} className="mb-6">
          <div className="flex justify-center gap-4 mb-4">
            <input
              type="text"
              placeholder="Name"
              value={newTodo.name}
              onChange={(e) => setNewTodo({ ...newTodo, name: e.target.value })}
              className="px-3 py-2 border rounded-md"
              required
            />
            <input
              type="text"
              placeholder="Description"
              value={newTodo.description}
              onChange={(e) => setNewTodo({ ...newTodo, description: e.target.value })}
              className="px-3 py-2 border rounded-md"
            />
            <button
              type="submit"
              className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors"
            >
              Add Todo
            </button>
          </div>
        </form>

        {/* Add Property Form */}
        <div className="mb-6">
          <div className="flex justify-center gap-4 mb-4">
            <input
              type="text"
              placeholder="Property Name"
              value={newProperty.name}
              onChange={(e) => setNewProperty({ ...newProperty, name: e.target.value })}
              className="px-3 py-2 border rounded-md"
              required
            />
            <select
              value={newProperty.type}
              onChange={(e) => setNewProperty({ ...newProperty, type: e.target.value })}
              className="px-3 py-2 border rounded-md"
            >
              <option value="LITERAL">Literal</option>
              <option value="MULTISELECT">MultiSelect</option>
              <option value="SINGLE_SELECT">Single Select</option>
            </select>
            <button
              onClick={handleAddProperty}
              className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors"
            >
              Add Property
            </button>
          </div>
        </div>

        {/* Todo Item List*/}
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
                  {properties.map((property) => (
                    <TableHead key={property.id} className="text-center">{property.name}</TableHead>
                  ))}
                  <TableHead className="text-center">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
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
                    {/* {properties.map((property) => (
                      <TableCell key={property.id} className="text-center">
                        {todo.properties[property.name] || ""}
                      </TableCell>
                    ))} */}
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
                  className={`px-3 py-1 rounded-full text-sm ${selectedTodo.status === "DONE"
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
              {/* {properties.map((property) => (
                <p key={property.id} className="mb-4">
                  <strong>{property.name}:</strong> {selectedTodo.properties[property.name] || ""}
                </p>
              ))} */}
            </div>
          </div>
        </div>
      )}


    </div>
  );
}

export default App;
