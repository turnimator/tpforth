VARIABLE myq
myq " 192.168.7.100/5000" 128 NQCREATE !
: rd myq @ <NQ SEXEC ; 
: rl 100 0 DO rd CR LOOP ;
: t1 " This is t1" S. CR ;
: t2 " This is t2" S. CR ;
: t3 " This is t3" S. CR ;
: t4 " This is t4" S. CR ;
: t5 " This is t5" S. CR ;

