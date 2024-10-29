import React from "react";
import {TableRow, TableCell} from "./ui/table";

interface TodoItemProps {
  id: number;
  name: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  description: string; // Description field to display item details
  onStatusToggle: () => void; // Function to toggle status
}

const TodoItem: React.FC<TodoItemProps> = ({
  id,
  name,
  status,
  description,
  onStatusToggle,
}) => {
  return (
    <TableRow key={id}>
      <TableCell className="text-center">{id}</TableCell>
      <TableCell className="text-center">{name}</TableCell>
      <TableCell className="text-center">{description}</TableCell>
      <TableCell className="text-center">
        <span
          className={`px-3 py-1 rounded-full text-sm ${
            status === "DONE"
              ? "bg-green-100 text-green-800"
              : status === "IN_PROGRESS"
              ? "bg-yellow-100 text-yellow-800"
              : "bg-gray-100 text-gray-800"
          }`}
        >
          {status}
        </span>
      </TableCell>
      <TableCell className="text-center">
        <button
          onClick={onStatusToggle}
          className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
        >
          Toggle Status
        </button>
      </TableCell>
    </TableRow>
  );
};

export default TodoItem;
