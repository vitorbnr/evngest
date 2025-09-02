package com.vitorbnr.evngest.service.relatorio;

import com.vitorbnr.evngest.exception.RecursoNaoEncontradoException;
import com.vitorbnr.evngest.model.Evento;
import com.vitorbnr.evngest.model.Inscricao;
import com.vitorbnr.evngest.repository.EventoRepository;
import com.vitorbnr.evngest.repository.InscricaoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    public String gerarRelatorioInscritosCsv(Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado com o ID: " + idEvento));

        List<Inscricao> inscricoes = inscricaoRepository.findByEvento(evento);

        StringWriter stringWriter = new StringWriter();

        String[] headers = {"ID_INSCRICAO", "NOME_USUARIO", "EMAIL_USUARIO", "DATA_INSCRICAO"};

        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(headers))) {
            for (Inscricao inscricao : inscricoes) {
                csvPrinter.printRecord(
                        inscricao.getId(),
                        inscricao.getUsuario().getNomeDeUsuario(),
                        inscricao.getUsuario().getEmail(),
                        inscricao.getDataInscricao()
                );
            }
            csvPrinter.flush();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao gerar o relatório CSV: " + e.getMessage());
        }
    }
}
