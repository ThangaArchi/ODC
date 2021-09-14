/*
///////////////////////////////////////////////////////////////
//  LaunchPubApp.c     -- modified node class                //
//  ver 1.0                                                  //
//  Language:     C                                          //
//  Platform:     Micron Dual Pentium Pro 200, Win NT 4.0    //
//  Application:  CSE691 - Project #2 prototype              //
//  Author:       J V Rao                                    //
///////////////////////////////////////////////////////////////

  Operation
  ===============
  This piece of code will be Launched by a shell script that has 
  afs tokens with respect to a certain userid.
  Once launched it sets the userid of the running process as the
  userid of the afs cell user.
  The commandline argument is the name of the program that needs
  to be launched which is passed over as an argument to the
  shell script.


///////////////////////////////////////////////////////////////
//  Build Instructions                                       //
///////////////////////////////////////////////////////////////
//  Files Required: LaunchPubApp.c                           //
//  Compiler Command:                                        //
//    cc  LaunchPubApp.c -oLaunchPubApp                      //
///////////////////////////////////////////////////////////////

    Maintenance History:
    ==================== 
    ver 1.0 : 10 May 2003
   
*/

#include <stdio.h>
#include<unistd.h>
#include <sys/types.h>
#include <pwd.h>
extern char **environ;



void main( int ArgumentCount, char *ArgumentV[ ], char *EnvironmentPointer  ){
FILE*  testKlogfile ;
char xauth[80];
char shellcommand[80];

struct passwd *Password;
char list[30] ;
int  i, numread, numwritten;
int PID;

strcpy(xauth,"XAUTHORITY=");
strcpy(shellcommand,"./setprofile.sh ");
/* Reading the Command line Arguments */

printf( "Number of Arguments read = %d\n",ArgumentCount );
for(i=0;i<ArgumentCount;i++)
printf( "Argument = %s\n",ArgumentV[i]);

printf("\nInside PubApp.c\n");
/* Getting the Password struct to get the UID for the given  userid  */
Password =getpwnam(ArgumentV[1]);
printf("home dir%s", Password->pw_dir);
fflush(NULL);
printf("\nThis is shellcommand before forking %s\n",shellcommand);

fflush(NULL);
PID=fork();

if(PID==0){
 	printf("Inside CHILD Process  = %d\n",PID);
	printf("PID of CHILD PROCESS = %d\n",getpid());
	/* Setting the UID for the process */
	
	printf("\nThis is shellcommand after forking %s\n",shellcommand);
	
	printf("Setting User id=%d\n",setuid(Password->pw_uid));
	printf("Userid set to =%d\n" ,getuid());
	printf("\n");
        /* Setting the .Xauthorty Environment variable */ 
printf("\nThis is shellcommand after forkinbut b4 doing xauth %s\n",shellcommand);
	strcat(xauth,ArgumentV[3]);
	printf("This is xauth %s\n",xauth);
	putenv(xauth);

	/* Building the shell command */
	printf("This is shellcommand befor concatenating %s\n",shellcommand);
	
	strcat(shellcommand,Password->pw_dir);
	strcat(shellcommand," ");
	strcat(shellcommand,ArgumentV[2]);
        printf("This is shellcommand %s\n",shellcommand);
	fflush(NULL);
	/* Starting the shell to launch the viewer */

	/* execl("/usr/bin/ksh","-c" ,"/home/pubApp/setprofile.sh /afs/eda/u/jvrao xclock",0);*/
	 execl("/usr/bin/ksh","ksh","-c" ,shellcommand,0);


	printf( "Child Done !!\n");
	return ;
}
else{
	printf("Inside Parent Process PROCESS = %d\n",PID);
	printf("Starting to Wait on Child Process\n");
	wait(PID);
	printf("Parent Process Now Exiting \n");
	return;
}
 


}
