#Feature: Create a todo
#
#As a Todo List User
#I would like to create a todo item
#so that I can be reminder of this task.
#
#Scenario Outline: Create a First Todo (Normal Flow)
#
#Given no todos have been created
#When requesting the creation of todo list with the following name <name>
#Then the todo list with following name <name>
#
#Examples:
#  | name          |
#  | "Buy milk"    |
#  | "Wash dishes" |
