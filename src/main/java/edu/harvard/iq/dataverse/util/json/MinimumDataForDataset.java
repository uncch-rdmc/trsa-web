/*
 * Copyright 2018 Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.harvard.iq.dataverse.util.json;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class MinimumDataForDataset {
    String title;
    String author;
    String email;
    String description;
    String subject="Other";

    public MinimumDataForDataset() {
    }

    
    
    public MinimumDataForDataset(String title, String author, String email, String description) {
        this.title = title;
        this.author = author;
        this.email = email;
        this.description = description;
    }
    
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "\ntitle: " + title + ", \nauthor: " + author + " \nemail: " + email + " \ndescription: " + description + " \nsubject: " + subject + "\n";
    }
    
    
    
}
