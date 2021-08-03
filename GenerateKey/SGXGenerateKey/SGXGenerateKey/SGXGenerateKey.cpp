#include "SGXGenerateKey_t.h"

#include "sgx_trts.h"


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sgx_tseal.h>

//char encrypt_data[BUFSIZ] = "Data to encrypt";
char aad_mac_text[BUFSIZ] = "aad mac text";
char LongtermPwd[BUFSIZ] = "Data to encrypt";
//char* LongtermPwd;

void GeneratePassword(int pwd_size, int* random, char* Password, uint8_t* SealedPwd, uint32_t data_size)
{
	int i;
	//Password = (char *)malloc(pwd_size + 1);
	
	char pwdcont[] = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	for (i = 0; i < pwd_size; i++)
	{
		
		*(Password + i) = pwdcont[random[i]];
		*(LongtermPwd + i) = pwdcont[random[i]];
		
	}
	
	*(Password + i) = '\0';
	*(LongtermPwd + i) = '\0';
	
	//memcpy(Password, LongtermPwd, strlen(LongtermPwd)+1);
	
	//seal the pwd
	/*
	uint32_t sealed_data_size = sgx_calc_sealed_data_size((uint32_t)strlen(aad_mac_text), (uint32_t)strlen(LongtermPwd));
	
	uint8_t *temp_sealed_buf = (uint8_t *)malloc(sealed_data_size);
	

	sgx_status_t  err = sgx_seal_data((uint32_t)strlen(aad_mac_text), (const uint8_t *)aad_mac_text, (uint32_t)strlen(LongtermPwd), (uint8_t *)LongtermPwd, sealed_data_size, (sgx_sealed_data_t *)temp_sealed_buf);
	if (err == SGX_SUCCESS)
	{
		// Copy the sealed data to outside buffer
		memcpy(EncryptedPwd, temp_sealed_buf, sealed_data_size);
	}
	//free(temp_sealed_buf);
	//memcpy(EncryptedPwd, temp_sealed_buf, sealed_data_size+1);
	for (int j = 0; j < sealed_data_size; j++)
	{
		*(SealedPwd + j) = temp_sealed_buf[j];
		//EncryptedPwd[j] = temp_sealed_buf[j];
	}
	//free(temp_sealed_buf);
	*/
}

uint32_t get_sealed_data_size()
{
	return sgx_calc_sealed_data_size((uint32_t)strlen(aad_mac_text), (uint32_t)strlen(LongtermPwd));
}

sgx_status_t seal_data(uint8_t* sealed_blob, uint32_t data_size)
{
	uint32_t sealed_data_size = sgx_calc_sealed_data_size((uint32_t)strlen(aad_mac_text), (uint32_t)strlen(LongtermPwd));
	if (sealed_data_size == UINT32_MAX)
		return SGX_ERROR_UNEXPECTED;
	if (sealed_data_size > data_size)
		return SGX_ERROR_INVALID_PARAMETER;

	uint8_t *temp_sealed_buf = (uint8_t *)malloc(sealed_data_size);
	if (temp_sealed_buf == NULL)
		return SGX_ERROR_OUT_OF_MEMORY;
	sgx_status_t  err = sgx_seal_data((uint32_t)strlen(aad_mac_text), (const uint8_t *)aad_mac_text, (uint32_t)strlen(LongtermPwd), (uint8_t *)LongtermPwd, sealed_data_size, (sgx_sealed_data_t *)temp_sealed_buf);
	if (err == SGX_SUCCESS)
	{
		// Copy the sealed data to outside buffer
		memcpy(sealed_blob, temp_sealed_buf, sealed_data_size);
	}

	free(temp_sealed_buf);
	return err;
}

sgx_status_t unseal_data(const uint8_t *sealed_blob, size_t data_size)
{
	uint32_t mac_text_len = sgx_get_add_mac_txt_len((const sgx_sealed_data_t *)sealed_blob);
	uint32_t decrypt_data_len = sgx_get_encrypt_txt_len((const sgx_sealed_data_t *)sealed_blob);
	if (mac_text_len == UINT32_MAX || decrypt_data_len == UINT32_MAX)
		return SGX_ERROR_UNEXPECTED;
	if (mac_text_len > data_size || decrypt_data_len > data_size)
		return SGX_ERROR_INVALID_PARAMETER;

	uint8_t *de_mac_text = (uint8_t *)malloc(mac_text_len);
	if (de_mac_text == NULL)
		return SGX_ERROR_OUT_OF_MEMORY;
	uint8_t *decrypt_data = (uint8_t *)malloc(decrypt_data_len);
	if (decrypt_data == NULL)
	{
		free(de_mac_text);
		return SGX_ERROR_OUT_OF_MEMORY;
	}

	sgx_status_t ret = sgx_unseal_data((const sgx_sealed_data_t *)sealed_blob, de_mac_text, &mac_text_len, decrypt_data, &decrypt_data_len);
	if (ret != SGX_SUCCESS)
	{
		//free(de_mac_text);
		//free(decrypt_data);
		//return ret;
	}

	if (memcmp(de_mac_text, aad_mac_text, strlen(aad_mac_text)) || memcmp(decrypt_data, LongtermPwd, strlen(LongtermPwd)))
	{
		ret = SGX_ERROR_UNEXPECTED;
	}

	free(de_mac_text);
	free(decrypt_data);
	return ret;
}