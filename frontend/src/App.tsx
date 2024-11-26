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
interface PropertyValue {
  id: number;
  value: string;
  propertyId: number;
}

interface TodoItemSpecificPropertyValues {
  id: number;
  name: string;
  type: string;
  values: PropertyValue[];
}

interface TodoItem {
  id: number;
  name: string;
  description: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  priority: "LOW" | "MEDIUM" | "HIGH";
  propertyValues: TodoItemSpecificPropertyValues[];
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
  const [isEditingDescription, setIsEditingDescription] = useState(false);
  const [editedName, setEditedName] = useState("");

  const [newTodo, setNewTodo] = useState({
    name: "",
    description: "",
    priority: "MEDIUM",
  });
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");

  const [editedDescription, setEditedDescription] = useState("");

  const [selectedTodo, setSelectedTodo] = useState<TodoItem | null>(null); // New state for selected todo
  const [newProperty, setNewProperty] = useState({ name: "", type: "LITERAL" });
  const [editingProperty, setEditingProperty] = useState<{ todoId: number, propertyId: number } | null>(null);
  const [editedPropertyValue, setEditedPropertyValue] = useState("");

  // Fetch properties for the todo list
  const fetchProperties = async () => {
    try {
      const response = await fetch("http://localhost:8080/todolist/Tasks/properties");
      if (!response.ok) throw new Error("Failed to fetch property IDs");
      const propertyIds: number[] = await response.json();

      const propertyPromises = propertyIds.map(async (id) => {
        const propertyResponse = await fetch(`http://localhost:8080/property/${id}`);
        if (!propertyResponse.ok) throw new Error("Failed to fetch property");
        return propertyResponse.json();
      });

      const propertiesData: Property[] = await Promise.all(propertyPromises);
      console.log(propertiesData);
      setProperties(propertiesData);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to fetch properties");
    }
  };

  // State for filtering
  const [filterProperty, setFilterProperty] = useState<string>("literalPropertyValue");
  const [filterValue, setFilterValue] = useState<string>("");

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
    fetchProperties();
  }, [sortDirection]);

  // Handle filtering
  const handleFilter = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8080/todoItems/filter?property=${filterProperty}&value=${encodeURIComponent(filterValue)}`
      );
      if (!response.ok) throw new Error("Failed to filter todos");
      const data: TodoItem[] = await response.json();
      setTodos(data);
      setError(null); // Clear errors
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to filter todos");
      setTodos([]); // Clear todos on error
    } finally {
      setIsLoading(false);
    }
  };

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

  const handleDescriptionSubmit = async (
    id: number,
    newDescription: string
  ) => {
    if (!todos) return;

    try {
      const response = await fetch(
        `http://localhost:8080/todoItem/${id}/updateDescription`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ description: newDescription }),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      setTodos((prev) =>
        prev.map((item) =>
          item.id === id ? { ...item, description: newDescription } : item
        )
      );
      setSelectedTodo((prev) =>
        prev ? { ...prev, description: newDescription } : null
      );
      setIsEditingDescription(false);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to update description"
      );
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
    id: number,
    type: string
  ) => {
    if (e.key === "Enter") {
      if (type === "name") {
        handleNameSubmit(id);
      } else if (type === "description") {
        handleDescriptionSubmit(id, editedDescription);
      }
    } else if (e.key === "Escape") {
      setIsEditing(null);
      setEditedName("");
      setIsEditingDescription(false);
      setEditedDescription("");
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

  // Function to handle property value submission
  const handlePropertyValueSubmit = async (todoId: number, propertyId: number, value: string) => {
    try {
      const response = await fetch(`http://localhost:8080/todoItem/${todoId}/properties/value`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          propertyId: propertyId,
          type: "LITERAL", // Assuming type is LITERAL, adjust as needed
          valueId: null,
          value: value,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
      }

      // Fetch the updated todo item
      const updatedTodoResponse = await fetch(`http://localhost:8080/todoItem/${todoId}`);
      if (!updatedTodoResponse.ok) {
        const errorText = await updatedTodoResponse.text();
        throw new Error(errorText);
      }
      const updatedTodo: TodoItem = await updatedTodoResponse.json();

      // Update local state with the updated todo item
      setTodos((prev) =>
        prev.map((todo) => (todo.id === todoId ? updatedTodo : todo))
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update property value");
    }
  };

  const handlePropertyKeyPress = (e: React.KeyboardEvent<HTMLInputElement>, todoId: number, propertyId: number) => {
    if (e.key === "Enter") {
      handlePropertyValueSubmit(todoId, propertyId, e.currentTarget.value);
    } else if (e.key === "Escape") {
      // Handle escape key if needed
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

        {/* Filter Form */}
        <form onSubmit={handleFilter} className="mb-6">
          <div className="flex justify-center gap-4 mb-4">
            <select
              value={filterProperty}
              onChange={(e) => setFilterProperty(e.target.value)}
              className="px-3 py-2 border rounded-md"
            >
              <option value="literalPropertyValue">Category</option>
              {/* Add more options for additional filter properties if needed */}
            </select>
            <input
              type="text"
              placeholder="Filter Value"
              value={filterValue}
              onChange={(e) => setFilterValue(e.target.value)}
              className="px-3 py-2 border rounded-md"
              required
            />
            <button
              type="submit"
              className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 transition-colors"
            >
              Filter
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
                  <TableHead className="text-center">ID</TableHead>
                  <TableHead className="text-center">Name</TableHead>
                  <TableHead className="text-center">Status</TableHead>
                  {properties.map((property) => (
                    <TableHead key={property.id} className="text-center">{property.name}</TableHead>
                  ))}
                  <TableHead className="text-center">
                    <div className="flex items-center justify-center gap-2">
                      Priority
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
                        onClick={toggleSort}
                        title={`Sort by priority ${sortDirection === "asc" ? "descending" : "ascending"
                          }`}
                      >
                        <ChevronsUpDown className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableHead>
                  <TableHead className="text-center">Toggle Status</TableHead>
                  <TableHead className="text-center">View Details</TableHead>
                  <TableHead className="text-center">Delete</TableHead>
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
                        properties={properties}
                        propertyValues={todo.propertyValues}
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
                        onKeyPress={(e) => handleKeyPress(e, todo.id, "name")}
                        onPropertyValueSubmit={handlePropertyValueSubmit}
                        onPropertyKeyPress={handlePropertyKeyPress}
                      />
                    ))}
                  </SortableContext>
                </DndContext>
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
      {selectedTodo && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center">
          <div className="p-8 bg-white shadow-lg rounded-lg text-center w-3/4 max-w-2xl">
            <div className="flex justify-end mb-4">
              <button
                onClick={() => setSelectedTodo(null)}
                className="text-gray-500 hover:text-gray-700"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
            <div className="text-3xl font-bold mb-6">Todo Details</div>
            <div className="text-lg space-y-4">
              <p>
                <strong>ID:</strong> {selectedTodo.id}
              </p>
              <p>
                <strong>Name:</strong> {selectedTodo.name}
              </p>
              <p>
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
              <div>
                <strong>Description:</strong>{" "}
                {isEditingDescription ? (
                  <div className="mt-2">
                    <input
                      type="text"
                      value={editedDescription}
                      onChange={(e) => setEditedDescription(e.target.value)}
                      onKeyDown={(e) =>
                        handleKeyPress(e, selectedTodo.id, "description")
                      }
                      onBlur={() =>
                        handleDescriptionSubmit(
                          selectedTodo.id,
                          editedDescription
                        )
                      }
                      className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                      autoFocus
                    />
                  </div>
                ) : (
                  <div className="mt-2 group flex items-center justify-center gap-2">
                    <span>{selectedTodo.description}</span>
                    <button
                      onClick={() => {
                        setIsEditingDescription(true);
                        setEditedDescription(selectedTodo.description);
                      }}
                      className="opacity-0 group-hover:opacity-100 text-blue-500 hover:text-blue-600"
                    >
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                        />
                      </svg>
                    </button>
                  </div>
                )}
              </div>
              <p>
                <strong>Priority:</strong>{" "}
                <span
                  className={`px-3 py-1 rounded-full text-sm ${selectedTodo.priority === "HIGH"
                    ? "bg-red-100 text-red-800"
                    : selectedTodo.priority === "MEDIUM"
                      ? "bg-yellow-100 text-yellow-800"
                      : "bg-green-100 text-green-800"
                    }`}
                >
                  {selectedTodo.priority}
                </span>
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
