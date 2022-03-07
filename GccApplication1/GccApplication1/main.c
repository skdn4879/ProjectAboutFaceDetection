/*
 * GccApplication1.c
 *
 * Created: 2021-09-04 오후 8:11:47
 * Author : KyPattern
 */ 

#include <avr/io.h>
#include <util/delay.h>

#define F_CPU 16000000UL

#define CIRCULAR_MOTOR_GO 0x83
#define CIRCULAR_MOTOR_BACK 0x43
#define MOTOR_STOP 0x03
#define GATE_MOTOR_GO 0x13
#define GATE_MOTOR_BACK 0x23

char rx_flag = 1; //수신 체크를 위한 플래그

int is_on_led1 = 0; //LED1의 켜짐 여부 플래그
int is_on_led2 = 0; //LED2의 켜짐 여부 플래그

void USART1_init(unsigned int UBRR1){
	UBRR1H = (unsigned char)(UBRR1 >> 8);
	UBRR1L = (unsigned char)UBRR1;
	UCSR1B = (1 << RXEN1);
} //기본설정

char RX1_CH(void){
	while(!(UCSR1A & (1 << RXC1))); //수신이 완료될 때까지 대기
	rx_flag = 1;
	return UDR1; //수신된 문자 반환
}

int main(void)
{
	
	char rx_buf; //수신 데이터를 담을 변수
	int remDevice; //동작시킬 기기를 구분할 변수
	USART1_init(103);
	
	DDRA = 0x07;
	DDRB = 0x01;
	DDRC = 0xFF;
	
    /* Replace with your application code */
    while (1) 
    {
		rx_buf = RX1_CH();
		remDevice = (int)rx_buf - '0';
		
		if(rx_flag){ //수신되면
			rx_flag = 0; //플래그 초기화
			
			if(remDevice < 5){
				doSomethingOn(remDevice);
			} else if(remDevice >= 5){
				doSomethingOff(remDevice);
			}
			
		}
		
    }
}

void doSomethingOn(int deviceNum){
	if(deviceNum == 1){
		if(is_on_led2 == 0){ //LED2가 꺼진 상태이면
			PORTA = 0x01; //LED1만 킴
			is_on_led1 = 1; //플래그 활성화
		} else{				//LED2가 켜진 상태이면
			PORTA = 0x03;	//둘 다 킴
			is_on_led1 = 1; //플래그 활성화
		}
		
	} else if(deviceNum == 2){
		if(is_on_led1 == 0){
			PORTA = 0x02;
			is_on_led2 = 1;
		} else{
			PORTA = 0x03;
			is_on_led2 = 1;
		}
		
	} else if(deviceNum == 3){
		PORTC = GATE_MOTOR_GO;
		_delay_ms(300);
		PORTC = MOTOR_STOP;
		_delay_ms(50000);
		PORTC = GATE_MOTOR_BACK;
		_delay_ms(300);
		PORTC = MOTOR_STOP;
	} else if(deviceNum == 4){
		PORTC = CIRCULAR_MOTOR_GO;
	} else if(deviceNum == 0){
		PORTA = 0x03;
		is_on_led1 = 1;
		is_on_led2 = 1;
		PORTC = CIRCULAR_MOTOR_GO;
	}
}

void doSomethingOff(int deviceNum){
	if(deviceNum == 6){
		if(is_on_led2 == 1){
			PORTA = 0x02;
			is_on_led1 = 0;
		} else{
			PORTA = 0x00;
			is_on_led1 = 0;
		}
		} else if(deviceNum == 7){
			if(is_on_led1 == 1){
				PORTA = 0x01;
				is_on_led2 = 0;
			} else{
				PORTA = 0x00;
				is_on_led2 = 0;
			}
		PORTA = 0x00;
	} else if(deviceNum == 8){
		PORTC = MOTOR_STOP;
	} else if(deviceNum == 9){
		PORTC = MOTOR_STOP;
	} else if(deviceNum == 5){
		PORTA = 0x00;
		is_on_led1 = 0;
		is_on_led2 = 0;
		PORTC = MOTOR_STOP;
	}
}

