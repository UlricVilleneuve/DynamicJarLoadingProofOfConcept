# DynamicJarLoadingProofOfConcept
This is a proof of concept. The goal is to have a central application dynamically loading modules (like plug ins) and running them.

Proof Of Concept

(x) Using SQLite

(x) Putting connection string to DB in .config file

(x) Loading the jar from a folder and finding the class

(x) Instantiating + Using DB

Functional, but the main app loading the modules needs to have the library to use SQLite even if its usage is in the module classes...
For now, it will remain like this, but maybe we can investigate and create a custom ClassLoader to overcome this.
