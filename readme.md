# code_onine
结合本学期所学的编译原理的内容，完成了一个简单的C语言编译器。其功能包括：实现词法分析、语法分析、语义分析、中间代码生成、优化和目标代码生成各阶段功能

> 所使用文法如下 
* 文法开始
 
      S->int main(){A}  
* 声明

      X->YZ;
      Y->int|char|bool
      Z->UZ’	
      Z’->,Z|$	
      U->idU’		
      U’->=L|$
* 赋值

      R->id=L;
      
* 算术运算
    
      L->TL’
      L’->+L|-L|$
      T’->*T|/T|$	
      F->(L)
      F->id|num
      O->++|--|$
      Q->idO|$
      
* 控制语句
    
      E->HE’
      E’->&&E|$
      H->GH’	
      H’->||H		
      H’->$	G->FDF	
      G->(E)
      G->!E		
      D-><|>|==|!=

* 算术运算
    
      B->if (E){A}else{A}
      B->while(E){A}		
      B->for(YZ;G;Q){A}
      
* 功能函数
    
      B->printf(P);		
      B->scanf(id);	
      B->return	
      P->id|str|num
      
* 复合语句

      A->CA		
      C->X|B|R	
      A->$
 
> Technology used in this project: 
* Java
* SpringBoot
* thymeleaf
* html、css、JavaScript