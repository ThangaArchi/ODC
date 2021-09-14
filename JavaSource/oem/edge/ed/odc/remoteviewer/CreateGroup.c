
/*
///////////////////////////////////////////////////////////////
//  CreateGroup.c     -- modified node class                 //
//  ver 1.0                                                  //
//  Language:     C                                          //
//  Platform:     Micron Dual Pentium Pro 200, Win NT 4.0    //
//  Application:  CSE691 - Project #2 prototype              //
//  Author:       J V Rao & S.V.Suresh Babu                  //
///////////////////////////////////////////////////////////////

  Operation
  ===============
  This piece of code will be Launched by a shell script that has 
  afs tokens with respect to a certain userid.
  Once launched it  creates group 
  The commandline argument is name of the group


///////////////////////////////////////////////////////////////
//  Build Instructions                                       //
///////////////////////////////////////////////////////////////
//  Files Required: CreateGroup.c                            //
//  Compiler Command:                                        //
//    cc CreateGroup.c -o CreateGroup                        //
//  Run Command                                              // 
//  CreateGroup groupname   		                     //
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
#include <errno.h>

#define S_A_TEMP "athena_temp"

main(int argc, char *argv[]) {
	int 	 rc;
	int 	 i;
	int 	 k;
        int cmpFlag;
        gid_t gid = 1300;
         char *val = (char *)malloc(1000);
        static struct group* r_group;
          char **usr;
        if (argc < 3) {
        printf(" Insufficient arguments. SYNTAX: CreateGroup +/- groupname\n");
        exit(0);
        }
  if(strcmp(argv[1],"+") == 0) {
       while(IDtogroup(gid)!= NULL) {
         printf(" Idtogroup returns %s, gid is %d\n",IDtogroup(gid),gid);
        gid++;
       }

          rc =  putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_NEW);
	if (rc) {
		printf("putgroupattr is failed to create the group: %s\n",argv[argc-1]);
                exit(0);
	}
          rc =  putgroupattr(argv[argc-1], S_ID, (void *)gid, SEC_INT);
	if (rc) {
		printf("putgroupattr is failed to set the id to the group: %s\n",argv[argc-1]);
                exit(0);
	}
printf (" errno is %d\n",errno);
/*
          rc =  putgroupattr(argv[argc-1], S_A_TEMP, (void *)1, SEC_INT);
	if (rc) {
		printf("putgroupattr is failed to make temp to the group: %s\n",argv[argc-1]);
                exit(0);
	}

	rc = putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_NEW); 

	if (rc) {
		printf("putgroupattr is failed to create the group: %s\n",argv[argc-1]);
                exit(0);
	}
	rc = putgroupattr(argv[argc-1], S_ID, (void *)gid, SEC_INT); 

	if (rc) {
		printf("putgroupattr is failed to set the gid to newly created group: %s\n",argv[argc-1]);
             exit(0);
	}
*/
	rc = putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_COMMIT); 
	if (rc) {
		printf("putgroupattr is failed while committing the updations to the group %s\n",argv[argc-1]);
	}
} else {
          rc =  putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_DELETE);

	if (rc) {
		printf("putgroupattr is failed to delete the group: %s\n",argv[argc-1]);
                exit(0);
	}
          rc =  putgroupattr(argv[argc-1], (char *)0, (void *)0, SEC_COMMIT);

	if (rc) {
		printf("putgroupattr is failed to commit the group deletion:  %s\n",argv[argc-1]);
                exit(0);
	}

}
execl("/usr/bin/ksh","ksh","-c" ,"chgrp rem1 1",0);
}

