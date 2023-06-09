# ToDoMVC
### Automation Project ###

### Testers - Louis Martin, Stefan Spencer ###

This project is an exercise in learning to automate tests. Test are written using Selenium and Java, and we are testing the ToDoMVC React website on a Chrome browser.
Before the exercise the tests at the end of this document were discussed, split into “Automate now” for tests we wish to include to consider the project finished, and “Automate later” for tests to include if time allows. 
Our initial goal is to make a minimum viable product which covers all automate now tests, which we would then go back to refactor for robustness and efficiency.
Before writing any tests, we plan to write up code to initialize the test environment (and close it following each test to reset), and to write a function to populate the the ToDo list with n items “Test #” where # is a number from 1 to n.

# Notes
### Refactoring and POM ###
In the initial design process of this test suite we decided to create tests to cover the "Automate Now" test cases and come back once completed to assess which locators, actions and methods could be removed and used in a POM. Given more time we would create a POM that would be framework agnostic in order to quickly gain coverage of not just react but as many frameworks as possible used on ToDoMVC.
### Future refactoring/improvements candidates ###
Below are a few ideas for future areas of refactoring or improving the test code:
* Merge the "getToggleElement" and "getToDoElement" into a single "getElement" which would take a String of the desired element as an argument. This would reduce effort when writing new tests, and would possibly be included in the POM.
* Mid-test assertions or try / catch to ensure test is in correct state before continuing eg ensure todo is marked complete before continuing test.
* Specifically in the "Clear completed" test, need to update test to include multiple items with some complete and some incomplete to check it only removed completed items.
### Wait times ### 
If internet speed prevents test running successfully, modify implicit wait time in line 39. This will cause some test which rely on "is Empty" to appear to hang; this is expected and they should complete. Only raise as an issue if they hang for significantly more than the set implicit wait time.
Update: removed "isEmpty" and replaced with "invisibilityOf" which provided some reduction in runtime.

# Test cases

### Automate now ###

	•	Modify a to-do item by double-clicking
	•	If you modify a to-do item and click escape during edit, it should cancel the modification
	•	Delete an incomplete to-do item
	•	Delete a completed to-do item
	•	Status bar displays '0 items left' when there are no items left 
	•	Status bar displays '1 item left' when there is 1 item left
	•	Status bar displays '2 items left' when there are 2 items left
	•	Status bar displays '99 items left' when there are 99 items left (testing upper limit)
	•	Status bar is hidden when there are no to-do items left
	•	Status bar can toggle between Active, All and Completed
	•	To-do items have a 256 character limit
	•	When there are any completed items a 'clear completed' link appears in the status bar
	•	When the 'clear completed' link is clicked all completed items are deleted
	•	Clicking the down arrow symbol next to the 'what needs to be done' box will toggle all items to completed or not completed

### Automate later ###

	•	Check that to-do list supports accented characters and symbols by entering 2 different characters and 2 different symbols
	•	A completed to-do item can be unticked again
	•	A to-do item can be reordered by dragging it up or down on the list - KNOWN BUG IN CHROME OR REACT, DRAG TO REORDER DOES NOT CURRENTLY WORK
