# finalpoe
1. Introduction 
This document outlines the complete plan and design for "TasteMapper," an Android 
application designed to help users discover, review, and add restaurants using the 
powerful Mapbox platform. It details functional requirements, UI/UX design, system 
architecture, data models, and project scheduling to guide development. 
2. App Overview 
• Name: TasteMapper 
• Icon:  
 
• Description: TasteMapper is an innovative restaurant discovery app that puts the 
community in charge. Using precise Mapbox maps, users explore nearby eateries.  
 
• The key innovation is the ability for any user to add a missing restaurant directly 
to the map, complete with details, photos, and their first review, instantly 
contributing to the shared knowledge base. The app will also teach user 
preferences to offer personalized suggestions. 
 
3. Detailed Requirements 
• User Authentication (Firebase Auth):  
Users must register/login via email or Google Sign-In to contribute content. This 
prevents spam and links reviews to a user profile. 
 
• Map-Based Discovery (Mapbox SDK):  
The home screen will be an interactive map. The app must request location 
permissions to center the map on the user and plot nearby restaurants as custom 
markers. 
 
• Restaurant List View:  
Users can toggle to a list view sorted by proximity or rating. Each list item will 
show the name, rating, cuisine type, and distance. 
 
• Add New Restaurant:  
A core feature. Users can tap a floating action button, place a pin on the map, 
and be taken to a form to input Name (String), Address (String), Cuisine Type 
(String from a list), Description (String), and upload Photos (Bitmap/Uri). This will 
be called our custom API. 
 
• View Restaurant Details:  
Tapping a marker/list item opens a detailed screen showing all the above 
information, plus an average rating and a list of user reviews. 
 
• Review and Rating System:  
Users can leave a star rating (1-5) and a text review (String) on a restaurant's 
detail page. 
 
• Personalized Feed ("For You"):  
A separate tab will show recommendations based on the cuisines and restaurants 
the user has previously liked or reviewed, requiring a simple recommendation 
algorithm. 
 
• User Profile:  
A screen where users can see their history of contribution (added restaurants, 
written reviews).
 Use of AI Tools in Assessment Completion 
AI tools, specifically ChatGPT and DeepSeek, were used as collaborative assistants under 
strict ethical guidelines to enhance the efficiency and depth of this assessment. Their 
role was guided entirely by my own analysis and design expertise, and they were not 
used to generate code or complete sections autonomously. 
My use of AI was strategic and focused on three key areas: 
1) Research Augmentation: For Part 1, I defined the analytical framework but used 
the LLMs as a "Research Analyst" to rapidly synthesize publicly available technical 
information on the chosen applications (Google Maps, Yelp, Foursquare). This 
helped generate a broad list of potential implementation technologies (e.g., APIs, 
databases) for my consideration. I then critically evaluated, verified, and curated 
these suggestions to ensure the final analysis was accurate and relevant. 
2) System Design Assistance: For Part 2, I conceived the core "TasteMapper" 
concept and selected the technical stack. I then prompted the AI to act as a 
"System Architect Assistant" to help draft a logically consistent Firestore data 
schema and propose a component diagram structure based on my requirements. 
This output served only as a initial draft; I designed the final, detailed schema and 
entire system architecture, ensuring it was robust and met all functional needs. 
3) Articulation and Drafting: The LLMs were used as an editorial tool to help 
articulate my predefined ideas and structured outlines into clear, professional 
prose, particularly for introductory and descriptive passages. Every output was 
thoroughly reviewed, edited, and rewritten to ensure it accurately reflected my 
original analysis and personal voice. 
Throughout this process, I maintained full human oversight. All critical thinking, 
synthesis of ideas, final decision-making, and quality control were my own. The AIs 
functioned as powerful tools for brainstorming and drafting, but the intellectual 
direction and final output remain a product of my own work and understanding, 
presented with full transparency. 
10. Conclusion 
This document provides a robust blueprint for developing TasteMapper. By following 
this plan, the development process will be structured and efficient, resulting in a fully 
featured, innovative application that meets all outlined requirements and leverages the 
chosen technologies effectively.
