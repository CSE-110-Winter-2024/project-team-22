# User Stories

## End-to-End Scenario: Jessica Struggles, and becomes a serial planner  

### US1: Frequency of the goal(one-time, daily, weekly, monthly, yearly) (10hrs, High)  

As a user, I want to set the frequency of the goal   
So that the goal can be added as one-time, daily, weekly, monthly, or yearly.  
  
Given a daily goal “Cook Dinner”   
When I press “+“  
Then I Type in “Cook Dinner” and press Daily  
When I press Save  
Then the goal “Cook Dinner” shows in Today, Tomorrow, and Recurring views  


![US1-1](images/US1.PNG)  

### US2: Categories/views for goals(Today, Tomorrow, Pending, Recurring) (9hrs, High)   

As a user, I want to have separate categories for the goals  
So that I can switch to Today, Tomorrow, Pending, or Recurring views.    
  
Given the goal “Cook Dinner” is in Tomorrow view  
When I press v  
Then I press Tomorrow  
And the view changes from Today to Tomorrow  
And “Cook Dinner” shows in Tomorrow view    
  
![US2-1](images/US2.PNG)  
  
### US3: Move goals from one category/view to another (4hrs, Low)    
  
As a user, I want to be able to move goals from one category/view to another  
So that I can adjust their category to feed my needs.  
  
Given the goal “Research plane tickets” in Pending view  
And I want to move it to Today view  
When I press and hold “Research plane tickets”  
And I press Move to Today  
Then the goal “Research plane tickets” is moved to Today view  
  
![US3-1](images/US3.PNG)  

### US4: Recurring goals (7hrs, Medium)  
  
As a user, I want to add a goal with start time and recurring frequency  
So that I can set the start time for recurring of the goal and recurring frequency(daily, weekly, monthly, yearly).  
  
Given a goal “Takeout Trash”   
And I want to recurring this goal weekly on Sunday  
When I press +  
Then I press Weekly on …  
When I press February 20,2024 and choose February 25,2024  
And I press Save  
Then the goal “Takeout Trash, weekly on Sunday” shows in Recurring  
  
![US4-1](images/US4.PNG)  

## End-to-End Scenario: Jessica becomes an expert Juggler  

### US5: Tag goals with a work context (Home, Work, School, or Errands) (8hrs, High)  
  
As a user, I want to be able to tag my goal with a work context  
So that I know what my to-do is related to (e.g., Home, Work, School, or Errands).  

Given “Cook Dinner” is a goal for “Home”   
When I press +  
Then I type in “Cook Dinner” and press H and press Daily  
When I press Save  
Then the goal “H Cook Dinner” shows in Today, Tomorrow, and Recurring views  

![US5-1](images/US5.PNG)  

### US6: Group goals by work context instead of order (4hrs, Low)  

As a user, I want my goals to be grouped by work context   
So that my to-do’s are more organized.  

Given a list of goals in the Today view  
When I open the Today view  
Then all goals are grouped by work context  

![US6-1](images/US6.PNG)  

### US7: Choose context focus with a Focus mode menu (8hrs, Medium)  

As a user, I want to have a Focus mode  
So that the app shows just the goals I want to finish right now.  

Given Today view have two “Home” goals, one “School” goal and one “Errands” goal  
When I press 三   
And I press H  
Then Today view shows two “Home” goals and archives other goals.  
And 三 changes to 三 (yellow)  

![US7-1](images/US7.PNG)  

### US8: Exit the Focus mode menu (2hrs, Medium)  

As a user, I want to be able to exit the Focus mode   
So that I can see my entire goal list when I need to.  

Given Today view shows two “Home” goals and archives other goals.  
When I press 三 (yellow)  
And I press Cancel  
Then Today view shows two “Home” goals, one “School” goal and one “Errands” goal  
And 三 (yellow) changes to 三  

![US8-1](images/US8.PNG)  

## Tasks for the first Iteration  

US1-1(5hrs, high): Implement UI component for goal input with frequency selection (one-time, daily, weekly, monthly, yearly).  
US1-2(3hrs, high): Create a function to save the goal with the selected frequency to the database.  
US1-3(2hrs, high): Update views (Today, Tomorrow, Recurring) to dynamically display goals based on their frequency.  

US2-1(3hrs, high): Implement UI components for Today, Tomorrow, Pending, and Recurring views.  
US2-2(4hrs, high): Develop functionality to filter and display goals in their respective views.  
US2-3(2hrs, high): Implement a view switcher to navigate between different goal views.  

US3-1(2hrs, low): Implement a drag-and-drop or selection mechanism for moving goals between views.  
US3-2(2hrs, low): Update the database to reflect the new category of the moved goal.  

US4-1(5hrs, medium): Implement UI for adding recurring goals with start time and frequency.  
US4-2(2hrs, medium): Create functionality to calculate and store recurring instances of the goal.  

Developer Story 1-1(8hrs, high): Finish MS1 left tasks.  

## Tasks for the second Iteration   

US5-1(3hrs, high): Create UI components for goal input with context tagging options (Home, Work, School, Errands).  
US5-2(2hrs, high): Implement functionality to store goals with their selected context in the database.  
US5-3(3hrs, high): Modify Today, Tomorrow, and Recurring views to display goals with their context tags.  

US6-1(2hrs, low): Implement functionality to sort and group goals by their work context in the views.  
US6-2(2hrs, low): Adjust the UI to visually distinguish between different work context groups.  

US7-1(4hrs, medium): Design and implement a Focus mode menu for selecting a specific work context.  
US7-2(2hrs, medium): Create functionality to filter and display only the goals related to the selected work context.  
US7-3(2hrs, medium): Implement a mechanism to archive (hide) goals not related to the selected context.  

US8-1(2hrs, medium): Implement functionality to exit Focus mode and return to the normal view, showing all goals.  









