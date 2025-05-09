# DataDino

![newlogo](https://github.com/user-attachments/assets/f9b30718-f87d-41f2-9c7c-a45d9ef6958b)

This blast from the past (2002-2003‼️) has been brought back to life using [Convirgance (JDBC)](https://github.com/InvirganceOpenSource/convirgance-jdbc) for 
modern database drivers. While mostly a curiousity today, DataDino is a fantastic time capsule of how Java development used to be in the Java 1.3 and 1.4 days. Some of 
the fun throwbacks you will find include:

- Pre-Collections code that uses Hashtables, Vectors, and (ew) Enumerators
- Manual boxing and unboxing of primitives
- MDI interface (remember those?)
- XML configuration
- Netbeans UI Designer forms

Despite the age of the tool, it is not bad at its job. It will let you navigate the database, update/delete data in place, create and delete tables, export 
SQL, and numerous other functions. It's probably not worth fully modernizing, but in its current form it actually gives a lot of free tools a run for their money!

## Installation

[![tryit](https://github.com/user-attachments/assets/fe2a935e-b72a-4eb8-973f-bfa283c4268e)](https://github.com/jbanes/DataDino/releases/download/2.2/datadino-2.2.zip)

Requires Java 21. Distributed as an executable JAR. 

Run with the following command:

    java -jar datadino.jar


## OG Screenshots

![screen6](https://github.com/user-attachments/assets/05e8aefd-2851-498a-ba50-e233ef24319d) ![screen3](https://github.com/user-attachments/assets/bb8a0843-8b14-46dc-b1fd-1d9cc4ff248b) ![screen2](https://github.com/user-attachments/assets/31a1b92f-4610-4464-9b66-d134d1e004ed)


## License

DataDino is source available for educational purposes. Please do not attempt to build anything on top of this codebase. You can do much better by building a modern tool 
using [Convirgance (JDBC)](https://github.com/InvirganceOpenSource/convirgance-jdbc) as the basis for connection management and drivers.
