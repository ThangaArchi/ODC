#include <fcntl.h>
#include <stdio.h>

void main(int ArgumentCount, char *ArgumentV[ ]) {
FILE* fp;
FILE* fw;
FILE* ft;
const int bufsize = 1024;
int filedesc;
int linelength =0;
int bytesread = 0;
char templine[1024];
char line[1024];
char* filecontents;
int found =0 ;
int obtainId=0;
int i=0;
fpos_t pos ;
off_t offset = 24;
if (ArgumentCount < 2){
printf( "Error: Usage ->  idManager obtainid OR idManager returnid id \n" );
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
		printf("Error:Missing Argument id to returnid");
		exit();
	}

}



filedesc = open("mappingfile",O_RDWR | O_NSHARE | O_DELAY | O_SYNC | O_DEFER );
if( filedesc == -1 )
   {
      printf( "Error:Opening File\n" );
      exit( 1 );
   }
if ((fp = fdopen(filedesc,"r"))==NULL)
	printf( "Error:Opening File Object for Reading" );

if ((fw = fdopen(filedesc,"w"))==NULL)
        printf( "Error:Opening File Object for Writing" );



fgetpos(fp,&pos);
/*printf("pos is %d\n",pos);*/

if (obtainId){
	while ( (fgets(line,80,fp)) != NULL){
		linelength = strlen(line);	
		if( (found == 0) && (line[0] == 'Y') && (obtainId ==1) ){
			/*printf("found an id");
			printf("linelength is %d\n",linelength;*/
			found = 1;
			line[0]='N';
			strcpy(templine,line);
			templine[linelength-1]='\0';
			strtok(templine,":");
			printf("%s",strtok(NULL,":"));
			printf(":");	
			printf("%s",strtok(NULL,":"));
			fsetpos(fw,&pos);
		/*	printf("pos in if is %d\n",pos);*/
			fwrite(line,1,linelength,fw);
			break;
		}
	fgetpos(fp,&pos);
	}
}
else {
	while ( (fgets(line,80,fp)) != NULL){
                 linelength = strlen(line);      
                if( (found == 0 ) && (strstr(line,ArgumentV[2]) !=  NULL ) ){
                        found = 1;
			line[0]='Y';
                        strcpy(templine,line);
                        strtok(templine,":");
			printf("%s",strtok(NULL,":"));
			fsetpos(fw,&pos);
                        fwrite(line,1,linelength,fw);
                        break;
                }
        fgetpos(fp,&pos);
        }
}
if ( ! found )
	printf("Error:Could not find the id in the file"); 

fflush(fp);		
fclose(fw);
	


}
