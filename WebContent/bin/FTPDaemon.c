#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <syslog.h>
#include <stdio.h>
#include <unistd.h>
#include <pwd.h>
#include <errno.h>
#include <signal.h>
#include <time.h>
#include <sys/time.h>
#include <stdlib.h>

#define SERV_LISTEN_PORT 5050
#define MAX_BUFF 5120                   /* 5K buff */
char* homedir = 0;
char* klog    = 0;

int dodebug = 0;

void getID(char buffer[], int len, int rfd, int wfd);

int port_to_use = SERV_LISTEN_PORT;
char* logfile = "/tmp/LFTdaemon_log";

unsigned char error_replyarr[] = {
   42,   0,   0,   0,  24,   0, 
    0,   0,  21, 
   'I', 'n', 'v', 'a', 'l', 'i', 'd', 
   ' ', 'u', 's', 'e', 'r', 'i', 'd', 
   '/', 'p', 'a', 's', 's', 'w', 'd' 
};

int init_daemon (){

  pid_t  pid;
  if ((pid = fork()) <0)
	print_to_log("error fork");
  else if (pid !=0)
	exit(0);
  
  setsid();
  chdir("/");
  umask(022);
 
  start_listen();
  return(0);
}

start_listen(){

  int sockfd, clilen, childpid;
  int newsockfd;
  struct sockaddr_in cli_addr, serv_addr;

  signal(SIGCHLD, SIG_IGN);
  /* Open an Internet stream socket */
  if ( (sockfd = socket (AF_INET, SOCK_STREAM, 0)) < 0 ) {
	print_to_log("Can't open stream socket");
        exit(3);
  }
  
  /* Bind port to socket */
  bzero ((char *) &serv_addr, sizeof(serv_addr));
  serv_addr.sin_family = AF_INET;
  serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
  serv_addr.sin_port = htons(port_to_use);

  if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
	print_to_log("server: can't bind local address");
        exit(3);
  }
        

  listen(sockfd, 5);

  for(;;){
   
    clilen =  sizeof(cli_addr);
    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, (unsigned long *)&clilen);
    
    if (newsockfd <0)
	print_to_log("server: accept error");
    if ((childpid = fork())<0 )
	print_to_log ("server: fork error");
    else if (childpid == 0) {
       close(sockfd);
       login_check(newsockfd, newsockfd);
       exit(0);
    }
    close(newsockfd); /*parent close the newsockfd*/
  }

}

login_check(int rfd, int wfd) {

  int nread;
  int i, len;
  int doCheck=1;
  char buff[MAX_BUFF];
  int keepread=1; 
  int read_amount=0;
  
  while(keepread){
    
     if (read_amount < MAX_BUFF){
        
        nread=read(rfd, buff+read_amount, (MAX_BUFF-read_amount));
       
        if (nread >= 1){   
          
           read_amount += nread;
           
           if (read_amount >= 5 && doCheck){   /*only check once*/
           
              if (buff[0]==2){
                 len=get_pkglen(buff);
                 doCheck=0;
              }
              else{
                 print_to_log("It is not a Login request.");
                 keepread=0;
                 close(rfd);
                 close(wfd);
              }
           }
          
           if (!doCheck && (read_amount==len+6))
              keepread=0;
             
        }
        else if (nread<0){
           print_to_log ("read error\n ");
           keepread=0;
           exit(3);
        }
        else if (nread == 0){ 
           print_to_log("stop read\n");
           keepread=0;
           exit(3);
        }
     }		  
     else {
        print_to_log("disconn\n");
        close(rfd); 
        close(wfd); 
        exit(5);
     } 
  }
  
 /* exam the data. expecting ID and PWD in the Login request*/
  if (buff[0]==2){ 
     if (((buff[1]& 0x01)==0) && ((buff[1] & 0x80)==0) ){   
        unsigned char* out;
        int i=0;
        int sz = sizeof(error_replyarr);
       
       /* Only returns if failed with authenticate */
        getID(buff, len, rfd, wfd);
        
        sleep(5);
        
        error_replyarr[5] = buff[5];
        while(i < sz) {
           int r = write(wfd, error_replyarr+i, sz-i);
           if (r < 0) exit(5);
           i += r;
        }
        
        login_check(rfd, wfd);
        exit(6);
        
     }   
     else{
        print_to_log("This login doesn't include ID/PWD\n");
        close(rfd);
        close(wfd);
     }
  }    
}

int get_pkglen(char buffer[]){
  
  int b0, b1, b2, length;
 
  b2 = (buffer[4]) & 0xff;
  b1 = (buffer[3] << 8) & 0xff00;
  b0 = (buffer[2]<<16) & 0xff0000 ; 
  length = b2 | b1 | b0;
  return (length);
  
}


void getID(char buffer[], int len, int rfd, int wfd){

  int p1, p2, i, j;
  int result;
  int id_len, pw_len;
  char id[MAX_BUFF];
  char pwd[MAX_BUFF];
  unsigned char handle;
  char handleStr[1024]; 
  
 /* id */
  p1=6;
  id_len =(buffer[7]& 0xff) | ((buffer[6]<<8) & 0xff00);
  p2=7+id_len;
  p1=p1+2;
  for (i=p1, j=0; i<=p2; i++){ 
	id[j]=buffer[i];
        id[++j]='\0';
  }
        
  
 /* pwd */ 
  pw_len= buffer[p2+1] & 0xff;
  p1=p2+2;
  p2=p1+pw_len;
  for (i=p1, j=0; i<p2; i++){
	pwd[j]=buffer[i];
        pwd[++j]='\0';
  }
  
  if (auth(id, pwd)){
    
     if(setEID(id)){
       
        handle=buffer[5];
        sprintf(handleStr, "%d", handle); 
        
        if (rfd != 0) {
           close(0);
           dup2(rfd, 0);
        }
        if (wfd != 1) {
           close(1);
           dup2(wfd, 1); /*duplicate write, read fd*/
        }
              
       /* If we should klog as this user ... 
       
          Actually ... think we get a token when we authenticate
          
        if (klog) {
           int pipes[2];
           int pid;
           pipe(pipes);
           pid = fork();
           if (pid) {
              int rc;
              char nl = '\n';
              FILE * out;
              close(pipes[0]);
              out = fdopen(pipes[1], "w");
              fwrite(pwd, strlen(pwd), 1, out);
              fwrite(&nl, 1, 1, out);
              fflush(out);
              waitpid(pid, &rc, 0);
              if (WIFEXITED(rc) && WEXITSTATUS(rc) == 0) {
                 ;
              } else {
                 if (WEXITSTATUS(rc) == 43) {
                    print_to_log("localAuth failed: Error with klog!\n");
                 }
              }
           } else {
              int devnull;
              devnull=open("/dev/null", O_WRONLY);
              dup2(pipes[0], 0);
              dup2(devnull,  1);
              dup2(devnull,  2);
              close(pipes[0]);
              close(devnull);
              execlp(klog, "klog", id, "-pipe", 0);
              exit(43);
           }        
        }
       */
        
        execlp("java", "java", "oem.edge.ed.odc.ftp.server.FTPServer", "-daemonStartup", id, homedir, handleStr, (char*)(dodebug?"-debug":0), (char*) 0);
         
     }      
     else{   
        print_to_log("server: seteuid error.");
     }   
     exit(4);
  }
}   




int setEID(char *uname){

  int result=0;
  struct passwd * pw = getpwnam(uname);
  if (pw!=NULL){
     int uid = pw->pw_uid;
     int gid = pw->pw_gid;
     
     homedir = (char*)malloc(strlen(pw->pw_dir)+1);
     strcpy(homedir, pw->pw_dir);
     
     if (!setgid(gid)) {
        if (!initgroups(uname, gid)) {
           if (!setuid(uid)) {
              result = 1;
           } else {
              fprintf(stderr, "setuid err = %d\n", errno);
           }
        } else {
           fprintf(stderr, "initgroups err = %d\n", errno);
        }
     } else {
        fprintf(stderr, "setgid err = %d\n", errno);
     }
  } else {
     print_to_log("user does not exist.\n");
  }
  return(result);
}


print_to_log(char* message){

  FILE *fp;
  char *strtime;
  long now, time();
  now = time((long *)0);
  strtime=ctime((const int*)&now);
  fp= fopen(logfile, "a");
  fputs(strtime, fp);
  fputs(message, fp); 
  fflush(fp);
  fclose(fp);
}  

int auth(char* uid, char* pwd){
  int i = 1;
  int rc = 55;
  char *buf=0;
  
  if (*pwd) {
	if (!(rc=authenticate(uid, pwd, &i, &buf)) && !i) {
	        return(1);
	} else if (rc < 0) {
		print_to_log("error\n");
		return (0);
	} else if (rc >0) {
		const char* tp = "error = %d ans=%s\n";
               /*printf("buf = %x\n", buf);
                 printf("error = %d\n\n%s\n", rc, buf);*/
		return(0);
        } 
  }
  else {
	return(0);
  }
}

main(int argc, char* argv[]) {
   int i;
   int inetd=0;
   klog=getenv("FULLPATHTOKLOG");
   for (i=1; i < argc; i++) {
      if (!strcmp("-port", argv[i])) {
         port_to_use = atoi(argv[++i]);
         if (port_to_use <= 0) {
            fprintf(stderr, "Port value not valid!\n");
            exit(5);
         }
      } else if (!strcmp("-inetd", argv[i])) {
         inetd=1;
      } else if (!strcmp("-klog", argv[i])) {
         klog=argv[i];
      } else if (!strcmp("-logfile", argv[i])) {
         logfile = argv[++i];
      } else if (!strcmp("-debug", argv[i])) {
         dodebug = 1;
      } else {
         fprintf(stderr, "Invalid parm = %s\n", argv[i]);
      }
   }
   
   
   if (inetd) {
      fclose(stderr);
      fopen("/tmp/ftpout", "w");
      signal(SIGCHLD, SIG_IGN);
      setsid();
      chdir("/");
      umask(022);
      login_check(0, 1);
   } else {
      print_to_log("start the LFT daemon...\n");
      init_daemon();
   }
}
