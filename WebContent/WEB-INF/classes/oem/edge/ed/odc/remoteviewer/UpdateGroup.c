/*
///////////////////////////////////////////////////////////////
//  UpdateGroup.c     -- modified node class                //
//  ver 1.0                                                  //
//  Language:     C                                          //
//  Platform:     Micron Dual Pentium Pro 200, Win NT 4.0    //
//  Application:  CSE691 - Project #2 prototype              //
//  Author:       J V Rao & S.V.Suresh Babu                             //
///////////////////////////////////////////////////////////////

  Operation
  ===============
  This piece of code will be Launched by a shell script that has 
  afs tokens with respect to a certain userid.
  Once launched it  adds/removes user(s) to a particular group
  The commandline arguments are name of the users those needs
  to be add/remove to/from the group as an argument to the
  shell script.


///////////////////////////////////////////////////////////////
//  Build Instructions                                       //
///////////////////////////////////////////////////////////////
//  Files Required: UpdateGroup.c                            //
//  Compiler Command:                                        //
//    cc UpdateGroup.c -o UpdateGroup                        //
//  Run Command                                              // 
//  UpdateGroup +/- username  groupname                      //
///////////////////////////////////////////////////////////////

    Maintenance History:
    ==================== 
    ver 1.0 : 03 Sept 2004
   
*/


#include <stdio.h> 
#include <strings.h>
#include <string.h>
#include <usersec.h>
#include <grp.h>


main(int argc, char *argv[]) {
	int 	 rc;
	int 	 i;
	int 	 k;
        int cmpFlag;
         char *val = (char *)malloc(1000);
        static struct group* r_group;
          char **usr;
        if (argc < 4) {
        printf("Error:120:Missing Required Argument\n");
        exit(0);
        }
        r_group = getgrnam(argv[argc-1]);
       
        usr = (r_group->gr_mem);
        i = 0;
        while(usr[i]) {
         cmpFlag = 0;
         if (strcmp(usr[i], "") != 0){
               if((strcmp(argv[1],"-") == 0)) {
		     for(k=2; k<(argc-1);k++) {
		       if(strcmp(usr[i], argv[k]) == 0) {
                          cmpFlag = 1;
                          break;
                        }
                     }
                }
                if(cmpFlag == 0) {
                if(strcmp(val,"") != 0)
        	strcat(val, ",");
        	strcat(val, usr[i]);
                }
         }
        i++;
        }
        if (argc >= 3 && (strcmp(argv[1],"-") != 0)) {
             for(k=2; k<(argc-1);k++) {
                if(strcmp(val,"") != 0)
        	strcat(val, ",");
        	strcat(val, argv[k]);
             }
        }

	rc = putgroupattr(argv[argc-1], S_USERS, val, SEC_LIST); 

	if (rc) {
		printf("Error:122:putgroupattr is failed to update the group: %s\n",argv[argc-1]);
                return;
	}
	rc = putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_COMMIT); 
	if (rc) {
		printf("Error:122:putgroupattr is failed while committing the updations to the group %s\n",argv[argc-1]);
              	return;
	}
        printf("success");
        
}

