// TodoItem.tsx
import React from "react";
import {TableRow, TableCell } from "./ui/table";

interface TodoItemProps {
  id: number;
  name: string;
  status: "NOT_DONE" | "DONE" | "IN_PROGRESS";
  // isEditing: boolean;
  // editedName: string;
  // onNameChange: (newName: string) => void;
  // onNameSubmit: () => void;
  // onStatusToggle: () => void;
  // onEditStart: () => void;
  // onCancelEdit: () => void;
}

// const TodoItem: React.FC<TodoItemProps> = ({
//   id,
//   name,
//   status,
//   isEditing,
//   editedName,
//   onNameChange,
//   onNameSubmit,
//   onStatusToggle,
//   onEditStart,
//   onCancelEdit,
// }) => {
//   const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
//     if (e.key === "Enter") {
//       onNameSubmit();
//     } else if (e.key === "Escape") {
//       onCancelEdit();
//     }
//   };

//   return (
//     <TableRow key={id}>
//       <TableCell className="text-center">{id}</TableCell>
//       <TableCell
//         className="text-center cursor-pointer hover:bg-gray-50"
//         onClick={() => {
//           if (!isEditing) {
//             onEditStart();
//           }
//         }}
//       >
//         {isEditing ? (
//           <input
//             type="text"
//             value={editedName}
//             onChange={(e) => onNameChange(e.target.value)}
//             onKeyDown={handleKeyPress}
//             onBlur={onNameSubmit}
//             className="w-full px-2 py-1 text-center border rounded"
//             autoFocus
//           />
//         ) : (
//           <span className="hover:text-blue-600">{name}</span>
//         )}
//       </TableCell>
//       <TableCell className="text-center">
//         <span
//           className={`px-3 py-1 rounded-full text-sm ${
//             status === "DONE"
//               ? "bg-green-100 text-green-800"
//               : status === "IN_PROGRESS"
//               ? "bg-yellow-100 text-yellow-800"
//               : "bg-gray-100 text-gray-800"
//           }`}
//         >
//           {status}
//         </span>
//       </TableCell>
//       <TableCell className="text-center">
//         <button
//           onClick={onStatusToggle}
//           className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 transition-colors"
//         >
//           Toggle Status
//         </button>
//       </TableCell>
//     </TableRow>
//   );
// };

// export default TodoItem;
export type {TodoItemProps};
