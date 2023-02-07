# Module 2: Develop

## Develop phase

“Shifting left” is a key tenant of the DevSecOps mindset, and this process begins well before code is even committed into a repository and deployed via a pipeline, by adopting secure coding best practices and using IDE (integrated development environments) tools & plugins for code analysis during the development phase can go a long way with addressing security issues earlier in the development lifecycle when it's much easier to fix.

### Best practice - Use Integrated Development Environment (IDE) plugins 

Most popular IDEs (Integrated Development Environments) like Visual Studio, Visual Studio Code, IntelliJ IDEA, and Eclipse support extensions that developers can use to get immediate feedback and recommendations around potential security issues they may have introduced while writing their application code

### Lab Module 1 - Install VS Code plugin

Install the Visual Studio Code plugin to analyze your application code for common security flaws

1. Launch VS Code on your computer
2. In the left panel, click on the extensions button (Ctrl+Shift+X) and type "Snyk" in the externsions search bar
3. Select the Synk Security - Code, Opens SOurce, IaC config extension as shown below and click on the install button
 
   ![DevSecOps Lifecycle Phases](../../assets/images/module2/snyk-extension.png)
4. The Visual Studio extension should now be enabled for all projects referenced


### Lab Module 2 - Download the OWASP vulnerable web app source code
1. Clone the OWASP WebGoat repo from GitHyb public repo https://github.com/WebGoat/WebGoat  The webGoat application is a sampke web app that illustrates many of the common vulenerabilities in web facing applications as determined by the OWASP Top Ten.
2. Go to the folder you have downloaded the project to /webGoat root folder and launch VS Code by typing "code ." from the root project directory
3. Optional - build the project
4. Look at "Problems panel within VS Code (Ctrl+Shift+M) to view the problems reported by the Snyk security extension, note: it may take a few minutes, the first time for Synk to analyze all the source code in your project
5. The problems panel shows all the details with the source file and line of code, the details can be fileterd by using the filter dropdown to limit the view from errors vs warnings. 
 ![Synk extension problems panel](../../assets/images/module2/snyk-annotated.png)













