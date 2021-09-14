#include <fcntl.h>
#include <stdio.h>

void main(int ArgumentCount, char *ArgumentV[ ]) {
FILE* fp;
FILE* fw;
FILE* ft;
FILE* fk;
const int bufsize = 1024;
int filedesc;
int mapfiledesc;
int linelength =0;
int bytesread = 0;
char appmapfile[1024];
char templine[1024];
char line[1024];
char* filecontents;
char appDet[2024];
int found =0 ;
int obtainId=0;
int i=0;
int sss=0;
int j=0;
fpos_t pos ;
off_t offset = 24;
char mapFilePath[80];
if (ArgumentCount < 3){
printf( "Error:120: Usage ->  updatemapfile obtainid AppName InstallDir OR updatemapfile returnid obtainid AppName id \n" );
exit(1);
}
printf( "Number of Arguments read = %d\n",ArgumentCount );
for(i=0;i<ArgumentCount;i++)
printf( "i is = %d, Argument = %s\n",i,ArgumentV[i]);


if( ! strcmp(ArgumentV[1], "obtainid"))
	obtainId = 1;
else {
	if (ArgumentCount < 4){
		printf("Error:120:Missing Argument id to returnid");
		exit();
	}
}
strcat(mapFilePath, "/home/pubApp/COMMON/mappingfile");

strcpy(appmapfile,"/home/pubApp/"); 
strcat(appmapfile,ArgumentV[2]);
strcat(appmapfile,"/mappingfile");
/* strcpy(appmapfile,"/home/pubApp/%s/mappingfile",ArgumentV[2]);*/
printf(" appmapfile is %s  ",appmapfile);


filedesc = open(mapFilePath ,O_RDWR | O_NSHARE | O_DELAY | O_SYNC | O_DEFER ); 
if( filedesc == -1 )
   {
      printf( "Error:121:Opening File\n" );
      exit( 1 );
   }
if ((fp = fdopen(filedesc,"r"))==NULL)
	printf( "Error:121:Opening File Object for Reading" );

if ((fw = fdopen(filedesc,"w"))==NULL)
        printf( "Error:121:Opening File Object for Writing" );

mapfiledesc = open(appmapfile ,O_RDWR | O_NSHARE | O_DELAY | O_SYNC | O_DEFER ); 
if( mapfiledesc == -1 )
   {
      printf( "Error:121:Opening File\n" );
      exit( 1 );
   }
if ((fk = fdopen(mapfiledesc,"w"))==NULL)
        printf( "Error:121:Opening File Object for Writing" );
fgetpos(fp,&pos);
printf("pos is %d\n",pos);
sss=atoi(ArgumentV[3]);
printf("the no of users%d\n\n",sss);
for(i=0;i<sss;)
{
if (obtainId){
	while ( (fgets(line,80,fp)) != NULL){
		linelength = (strlen(line));	
		if( (line[0] == 'N') && (obtainId ==1) ){
			/*printf("found an id");
			printf("linelength is %d\n",linelength;*/
			line[0]='Y';
                        found=1;  
			strcpy(templine,line);
			templine[linelength-1]='\0';
			strtok(templine,":");
			printf("%s",strtok(NULL,":"));
			printf(":");	
			printf("%s",strtok(NULL,":"));
		/*	fsetpos(fw,&pos);  */
			printf("pos in if is %d\n",pos);
			
			fwrite(line,1,linelength,fk);
			fwrite(line,1,linelength,fw);
           printf("the line is %s",line);		
	i++;
			break; 
		}
	fgetpos(fp,&pos);
	}
}
else {
	while ( (fgets(line,80,fp)) != NULL){
                 linelength = strlen(line);      
                if( (found == 0 ) && (strstr(line,ArgumentV[4]) !=  NULL ) ){
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
 }
if ( ! found )
	printf("Error:102:Could not find the id in the file"); 

fflush(fp);		
fclose(fw);
fflush(fk);		
fclose(fk);
	


}
