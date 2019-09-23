# CSCI-161-Student_Manager

This was the final project for the summer class CSCI-161 in 2018. The class submitted ideas of what they thought would be a fun project 
to develop. The professor took those suggestions and grabbed the most popular ones and randomly assigned students to different groups 
that were tasked with creating these projects. Our group was assigned to develop a student management program. We decided we wanted to 
try to have both an advisor portion of the application as well as the student portion.

So, students could type in their ID number and then they would be able to check the classes they had available, add the classes they 
wanted to their "cart" and then be able to register for the classes in their carts. The advisors could access the ability to see who was 
in their classes and remove students.

This was the first group project as a computer science student. It was a big challenge coding together on something and making sure 
everyone was on the same page. We worked really hard on this and I feel like we did a great job on it overall. It was not free of bugs, 
but we did manage to make it work pretty well given the circumstances of our experience and time allotted.

Notes:
- It auto-generates students and classes, building them from arrays of information. We were not introduced to databases of any kind.

- When loading the application, it will check for a file containing class / student information, if none is found it will create it's own 
  and auto-fill it with randomly generated information. Then, as you make changes to the students / courses, it will save those changes 
  to the file.
  
- Due to this being our first full GUI application in Java developing with the netbeans IDE, we had a huge learning curve in how to 
  handle our code. We settled for keeping all of the actual application's code within the GUI.java file.
  
- There are a lot of things I would attempt differently after this, especially after learning more about APIs and better methodologies in 
  general. :)
