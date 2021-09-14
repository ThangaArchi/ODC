/*
///////////////////////////////////////////////////////////////
//  changeowner.c     -- modified node class                //
//  ver 1.0                                                  //
//  Language:     C                                          //
//  Platform:     Micron Dual Pentium Pro 200, Win NT 4.0    //
//  Application:  CSE691 - Project #2 prototype              //
//  Author:       J V Rao & S.V.Suresh Babu                             //
///////////////////////////////////////////////////////////////

  Operation
  ===============
  This piece of code will change the owner of specified file


///////////////////////////////////////////////////////////////
//  Build Instructions                                       //
///////////////////////////////////////////////////////////////
//  Files Required: changeowner.c                            //
//  Compiler Command:                                        //
//    cc changeowner.c -o changeowner                        //
//  Run Command                                              // 
//  changeowner ownerid filename                               //
///////////////////////////////////////////////////////////////

    Maintenance History:
    ==================== 
    ver 1.0 : 03 Sept 2004
   
*/


#include <stdio.h>
#include <sys/types.h>
#include <dirent.h>
#include <ctype.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdlib.h>
#include <grp.h>
#include <pwd.h>
#include <sys/errno.h>



void reportError(int error,char *path);

main(int argc, char *argv[]) {
	int 	 rc;
        char *filepath = argv[2];
        struct stat buf;
        struct passwd *Password;
         uid_t user_id;
        if (argc < 3) {
        printf("Error:120:Insufficient arguments. SYNTAX: changeowner owner filename \n");
        exit(0);
        }
         Password =getpwnam(argv[1]);
         user_id = Password->pw_uid;
           rc = stat(argv[2], &buf);
        if(rc != 0)
         {
         reportError(errno,argv[2]);
         exit(0);
         }
       

         rc = chown(argv[2], user_id,-1);

         if(rc == -1) {
              reportError(errno,argv[2]);
          } else {
              printf("success");
          }

}
void reportError(int error,char *path)
{
	switch(error)	{
                case ENOTDIR :
                        printf("Error:123:A component of '%s' is not a directory\n",path)       ;
                        break   ;
                case ENAMETOOLONG :
                        printf("Error:123:A component or the full path '%s' is too long\n",path)        ;
                        break   ;
                case ENOENT :
                        printf("Error:123:The file '%s' doesn't exist\n",path)  ;
                        break   ;
                case ELOOP :
                        printf("Error:123:Too many symbolic links in '%s'\n",path)      ;
                        break   ;
                case EPERM :
                        printf("Error:123:Permission error '%s'\n",path)        ;
                        break   ;
                case EROFS :
                        printf("Error:123:Read-only file system '%s'\n",path)   ;
                        break   ;
                case EFAULT :
                        printf("Error:123:Invalid memory address\n")    ;
                        break   ;
                case EIO :
                        printf("Error:123:IO error accessing '%s'\n",path)      ;
                        break   ;
                default :
                        printf("Error:123:Changeowner Error %d on '%s'\n",errno,path)   ;
                        break   ;

	}
	errno = 0	;
}


