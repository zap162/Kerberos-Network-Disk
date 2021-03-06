#include <iostream>
#include <string>
#include <stdlib.h>
#include <time.h>

using namespace std;

//物品数目以及背包容量

static int PACK1 = 1000;
//物品总量
//static int tj1[10] = { 23,31,29,44,53,38,63,85,89,82 };
static int tj1[100] =  {42,30,27,93,8,34,47,64,82,76,70,79,23,5,67,9,97,29,7,61,73,3,44,85,7,51,49,90,59,38,55,39,62,85,54,81,38,42,90,90,26,50,22,71,52,41,77,32,49,2,96,84,20,48,17,62,87,94,84,26,73,52,12,70,42,47,94,13,47,89,90,7,51,39,24,6,74,69,5,47,78,65,67,35,89,69,96,15,20,8,28,25,16,33,22,
	16,64,64,63,67};

//物品价值
//static int val1[10] = { 92,57,49,68,60,43,67,84,87,72 };
static int val1[100] = { 15, 64, 82,87,81,54,65,98,42,99,6,50,90,
99,96,57,76,12,47,18,46,73,99,60,40,60,15,5,65,69,19,
72,51,33,11,72,69,64,97,95,32,59,34,3,27,99,82,44,66,83,
72,28,64,90,15,38,65,91,68,28,17,80,1,29,54,8,11,11,70,40,93,65,
51,49,75,35,41,60,72,57,76,6,28,12,59,58,55,30,66,13,24,24,9,17,43,67,90,37,36,36 };

static const  int PS = 100;//定义种群规模是500
static double PC = 0.8;                       //交叉率
static double PV = 0.2;                      //变异率
static int MAX_GENERATION = 1000000;             //遗传最大代数
static const int NUM1=100;//物品数目

class Individual
{
   public:

    int m_fit;//适应度
    int m_sum_w;//重量
    int m_sum_v;//体积
    int m_sum_val;//价值
    int m_gene[NUM1];
    int m_count;

    Individual(){//构造函数
    m_fit = 0;
    m_sum_v = 0;
    m_sum_w = 0;
    m_sum_val = 0;
    int i;
    for(i = 0;i < NUM1;i++)
        m_gene[i] = 0;//初始化基因
    }
};

class GA {
private:

	Individual m_zq[PS];                          //种群，ps是种群规模
	Individual m_max_single;                      //最优个体

public:

	//初始化种群
	void Init(int NUM, int PACK_MAX_V, int tj[]);

	//计算个体价值
	int Cal_SingleValue(int NUM, int row, int value[]);

    //计算个体体积
	int Cal_SingleV(int NUM, int row, int tj[]);

	//计算个体适应度
	void Cal_Fitness(int NUM, int tj[], int value[], int PACK_MAX_V);

	//计算价值最大个体
	void Cal_Maxval_Single(int _generation);

	//选择
	void Select();

	//是否交叉
	bool IsCross() { return ((rand() % 1000 / 1000.0) <= PC); }

	//交叉
	void Cross(int NUM);

	//是否变异
	bool IsVariation() { return ((rand() % 1000 / 1000.0) <= PV); }

	//变异
	void Variation(int NUM);

	//进行遗传，每五代几率变异
	void Run() {

		int i;
		Init(NUM1, PACK1,tj1);                      //初始化种群
		for (i = 0; i < MAX_GENERATION; i++) {
			Cal_Fitness(NUM1 ,tj1, val1, PACK1);    //计算适应度
			Cal_Maxval_Single(i);                   //计算价值最大个体
			Select();                               //选择
			Cross(NUM1);                            //交叉
			if (i % 5 == 0 && i != 0) {             //每五代变异
				Variation(NUM1);                    //变异
			}
		}
		Cal_Fitness(NUM1, tj1,val1, PACK1);         //计算适应度
		Cal_Maxval_Single(MAX_GENERATION);          //计算价值最大个体
		cout << "***********************First question***********************" << endl;
        cout << "Capacity: 1000" << endl;
        cout << "*" << endl;
		cout << "The best value is:" << m_max_single.m_fit << endl; //价值最大是
		cout << "The best entity's gene is:" << endl;               //最好的基因序列是
		for (int i = 0; i < NUM1; i++) {                            //输出基因序列
			cout << m_max_single.m_gene[i];
			if (i != NUM1 - 1)
				cout << " ";
		}
		//输出最好的迭代次数
		cout << endl << "The best entity is in the " << m_max_single.m_count << " generation." << endl;

	}
};

int main(){
    srand(time(NULL));//产生随机数种子
    GA temp;
    temp.Run();
    return 0;
}





//随机产生01
int pp(){
    float p;
    p = rand() % 1000 / 1000.0;
    if(p < 0.8)
        {//cout<<0<<" ";
        return 0;}
    else
        {//cout<<1<<" ";
        return 1;}
}

void GA::Init(int NUM,int PACK_MAX_V,int tj[]){//初始化,物品个数，背包容量，物体体积数组
    int i,j,vsum;
    for(i = 0;i < PS;i++){
        vsum = 0;//初始化总量为0
        for(j = 0;j < NUM;j++){
            m_zq[i].m_gene[j] = pp();//随机产生基因序列，即放与不放
            vsum += m_zq[i].m_gene[j]*tj[j];//对应位的基因乘以体积
            }
        if(vsum > PACK_MAX_V)    //产生符合条件的个体，即能放进去的
            i--;
    }
/*
    cout<<"Init:"<<endl;
    for (i = 0;i < PS;i++){
        for (j = 0;j < NUM;j++)
            cout<<zq[i].gene[j]<<" ";
        cout<<endl;
    }
*/
}

int GA::Cal_SingleValue(int NUM,int row,int value[]){//计算价值,输入物品个数,代数,价值数组
    int j,valuesum = 0;
    for(j = 0;j < NUM;j++){
        valuesum += m_zq[row].m_gene[j]*value[j];
    }
    m_zq[row].m_sum_val = valuesum;//算出子代适应度
    return valuesum;//返回
}

int GA::Cal_SingleV(int NUM,int row,int tj[]){//计算体积和,输入物品数，种代，体积数组
    int j,vsum = 0;
    for(j = 0;j < NUM;j++){
        vsum += m_zq[row].m_gene[j]*tj[j];
    }
    m_zq[row].m_sum_v = vsum;
    return vsum;
}

void GA::Cal_Fitness(int NUM,int tj[],int value[],int PACK_MAX_V){//计算所有子代适应度
    int i,v,val;//i是第多少代
    for(i = 0; i < PS; i++) {//计算所有子代
        v = Cal_SingleV(NUM,i,tj);//计算体及
        val = Cal_SingleValue(NUM,i,value);//计算价值
        if(v > PACK_MAX_V) { m_zq[i].m_fit = 0; continue; }//判断若超过总容积
        m_zq[i].m_fit = val;//计算出当前适应度，这里用的价值
        //cout<<zq[i].fit<<endl;
        }

}

void GA::Cal_Maxval_Single(int generation){//计算最好的
    int i,maxval = m_zq[0].m_fit,id = 0;
    for(i = 0;i < PS;i++)
        if(maxval < m_zq[i].m_fit){//若小于则赋新值
            maxval = m_zq[i].m_fit;
            id = i;
        }
    if(maxval > m_max_single.m_fit){//与类成员比较
        m_max_single = m_zq[id];
        m_max_single.m_count = generation;//代数
    }
}

void GA::Select(){

    int fit_sum = 0,i,j;
    float rand_rate,cur_rate;
    float selected_rate[PS];
    Individual new_zq[PS];//子代种群

    for(i = 0;i < PS; i++){
        fit_sum += m_zq[i].m_fit;//计算种群适应度和
    }
    //使用轮赌法进行选择
    selected_rate[0] = float(m_zq[0].m_fit) / fit_sum;//选择的概率即适应度所占比重

    for(i = 1; i < PS; i++){
        cur_rate = selected_rate[i-1] + float(m_zq[i].m_fit) / fit_sum;//cur_rate旧加新
        selected_rate[i] = cur_rate;
    }

    for(i = 0; i < PS; i++) {
        rand_rate = ( rand() % 1000 / 1000.0 );//？？？？？？？？？？？？？
        for (j = 0; j < PS; j++) {
            if(rand_rate <= selected_rate[j]) {//若随机产生的小于选择概率
                new_zq[i] = m_zq[j];//进入下一代
                break;
            }
        }
    }
    for(i = 0;i < PS;i++){
        m_zq[i]= new_zq[i];//用新种群覆盖之前的
        //cout<<zq[i].fit<<endl;
    }

}

void GA::Cross(int NUM){//杂交函数
    int i,j;
    for(i = 0;i < PS - 1;i += 2){
        Individual in1 = m_zq[i];//0,2,4,6..
        Individual in2 = m_zq[i + 1];//1,3,5,7..
        int j=rand()%NUM;
        for(j = 0;j < NUM; j++){
            if(IsCross()){
                int tmp = in1.m_gene[j];
                in1.m_gene[j] = in2.m_gene[j];
                in2.m_gene[j] = tmp;//交换基因完成杂交
            }
        }
        m_zq[i]=in1;
        m_zq[i+1]=in2;
    }
}

void GA::Variation(int NUM){//变异
    int i,j;
    for(i = 0;i < PS;i++){
        if(IsVariation()){
            for (j = 0;j < NUM;j++){
                if(IsVariation()){
                    m_zq[i].m_gene[j] = (m_zq[i].m_gene[j] ? 0 : 1);//
                }
            }
        }
    }
}
       