package school.sptech.megusta.service;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.sptech.megusta.dto.planilha_vendas.Vendas;

import java.io.IOException;
import java.util.List;

@Service
public class VendasService {

    public List<Vendas> lerPlanilha(MultipartFile planilha) throws IOException {
        return Poiji.fromExcel(
                planilha.getInputStream(),
                PoijiExcelType.XLSX,
                Vendas.class
        );
    }
}
