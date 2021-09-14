#include <fcntl.h>
#include <stdio.h>
#include <string.h>

void main(int ArgumentCount, char *ArgumentV[ ]) {
FILE* fp;
int filedesc;
int linelength =0;
char templine[1024];
char line[1024];
char *token;
int found =0 ;
int obtainId=0;
int i=0;
fpos_t pos ;
char mapFilePath[80];
int strtkncount=0;
if (ArgumentCount < 2){
printf( "Error:120: Usage ->  UserManager obtainid OR idManager returnid id \n" );
exit(1);
}
/*printf( "Number of Arguments read = %d\n",ArgumentCount );
for(i=0;i<ArgumentCount;i++)
printf( "Argument = %s\n",ArgumentV[i]);
*/

if( ! strcmp(ArgumentV[1], "obtainid"))
	obtainId = 1;
else {
	if (ArgumentCount < 3){
		printf("Error:120:Missing Argument id to returnid");
		exit();
	}

}
/* strcat(mapFilePath, "/home/pubApp/COMMON"); */
strcat(mapFilePath, ArgumentV[3]);
/* strcat(mapFilePath,ArgumentV[2]); */
strcat(mapFilePath, "/usermapinfofile");

filedesc = open(mapFilePath ,O_RDWR | O_NSHARE | O_DELAY | O_SYNC | O_DEFER ); 
if( filedesc == -1 )
   {
      printf( "Error:121:Opening File\n" );
      exit( 1 );
   }
if ((fp = fdopen(filedesc,"r"))==NULL)
	printf( "Error:121:Opening File Object for Reading" );


fgetpos(fp,&pos);
/*printf("pos is %d\n",pos);*/

if (obtainId){
	while ( (fgets(line,80,fp)) != NULL){
		linelength = strlen(line);	
		if( (found == 0) && (obtainId ==1) ){
			templine[linelength-1]='\0';
			strcpy(templine,line);
			token = strtok(templine,":");
                        while(token != NULL) {
			token = strtok(NULL,":");
                        strtkncount++;
                        }
			printf("%d:%s",strtkncount-2,line);
			found = 1;
		}
	fgetpos(fp,&pos);
	}
}
if ( ! found )
	printf("Error:102:Could not find the id in the file"); 

fflush(fp);		
	


}
